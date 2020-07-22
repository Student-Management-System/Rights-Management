package net.ssehub.rightsmanagement.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.Member;
import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.api.GroupsApi;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

/**
 * Pulls information from the <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class DataPullService {
    
    private static final Logger LOGGER = LogManager.getLogger(DataPullService.class);

    private String courseName;
    private String semester;
    private String courseID;
    private String tutorsGroupName;
    
    private CoursesApi courseAPI;
    private GroupsApi groupsAPI;
    private AssignmentsApi assignmentsAPI;
    
    /**
     * Creates a new {@link DataPullService} with the default URL of the student management system as specified
     * in the configuration.
     * @param config A configuration specifying which course information shall be pulled by this instance.
     */
    public DataPullService(CourseConfiguration config) {
        this(Settings.getConfig().getMgmtServerURL(), config.getCourseName(), config.getSemester());
    }
    
    /**
     * Creates a new {@link DataPullService}, which allows a more flexible configuration of its parameters.
     * Mainly designed for testing purpose.
     * @param serverURL The url of the student management system.
     * @param courseName The name of the course to pull the information from.
     * @param semester The semester to pull the information from.
     */
    public DataPullService(String serverURL, String courseName, String semester) {
        ApiClient client = new ApiClient().setBasePath(serverURL);
        if (null != Settings.INSTANCE.getLogin()) {
            client.setAccessToken(Settings.INSTANCE.getLogin().getManagementToken());
        }
        
        groupsAPI = new GroupsApi(client);
        assignmentsAPI = new AssignmentsApi(client);
        courseAPI = new CoursesApi(client);
        
        this.courseName = courseName;
        this.semester = semester;
        this.courseID = courseName + "-" + semester;
    }

    /**
     * Pulls the complete course information.
     * @return The complete course information
     */
    public Course computeFullConfiguration() {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setSemester(semester);
        
        Group tutors = createTutorsGroup();
        List<Member> studentsOfCourse = loadStudents(tutors);
        course.setTutors(tutors);
        course.setStudents(studentsOfCourse);
        
        // Gather all homework groups
        List<Group> homeworkGroups = loadGroups();
        course.setHomeworkGroups(homeworkGroups);
        
        // Collect assignments
        List<Assignment> assignments = loadAssignments(studentsOfCourse, homeworkGroups);
        course.setAssignments(assignments);
        
        return course;
    }
    
    /**
     * Creates an empty group to store the tutors of the course.
     * This method ensures that the Tutors get a unique / protected group name, that should not be used from
     * any homework group.
     * @return A new, empty group that is intended to store tutors.
     */
    public Group createTutorsGroup() {
        Group tutors = new Group();
        tutorsGroupName = "Tutors_of_Course_" + courseName.substring(0, 1).toUpperCase() + courseName.substring(1);
        tutors.setGroupName(tutorsGroupName);
        
        return tutors;
    }

    /**
     * Loads the list of known students from the course.
     * This is <i>only</i> needed to reduce the traffic.
     * This is useful as we need to iterate over all students while we iterate over all assignments.
     * Without cached members, we would call the student management system n² times.
     * @param tutors The group of tutors, will be changed as <b>side effect</b>, may be an empty group if this is not
     *     further processed.
     * @return The list of participating students
     */
    public List<Member> loadStudents(Group tutors) {
        List<Member> studentsOfCourse = new ArrayList<Member>();
        try {
            List<UserDto> usersOfCourse = courseAPI.getUsersOfCourse(courseID);
            for (UserDto userDto : usersOfCourse) {
                switch (userDto.getCourseRole()) {
                case STUDENT:
                    Member student = new Member();
                    student.setMemberName(userDto.getRzName());
                    studentsOfCourse.add(student);
                    break;
                case LECTURER:
                    // falls through
                case TUTOR:
                    tutors.addMembers(userDto.getRzName());
                    break;
                default:
                    LOGGER.warn("{} is an administrator and user of the course {}. Cannot handle this user.",
                        userDto.getRzName(), courseID);
                    break;
                }
            }
        } catch (ApiException e) {
            LOGGER.warn("Could not query student management system for Users via \""
                    + Settings.getConfig().getMgmtServerURL() + "\".", e);
        }
        return studentsOfCourse;
    }

    /**
     * Pulls the information of configured {@link Assignment}s from the <b>student management system</b>.
     * @param studentsOfCourse The list of known participants of the course.
     * @param homeworkGroups The list of know home work groups of the course.
     * @return The assignments of the course, containing the participants of the assignments (students in case of
     *     single assignments, otherwise the groups).
     */
    private List<Assignment> loadAssignments(Collection<Member> studentsOfCourse, List<Group> homeworkGroups) {
        List<Assignment> assignments = new ArrayList<>();
        try {
            List<AssignmentDto> assignmentsOfServer = assignmentsAPI.getAssignmentsOfCourse(courseID);
            for (AssignmentDto assignmentDto : assignmentsOfServer) {
                try {
                    Assignment assignment = new Assignment(assignmentDto);
                    if (assignment.isGroupWork()) {
                        assignment.addAllParticipants(homeworkGroups);                        
                    } else {
                        assignment.addAllParticipants(studentsOfCourse);                        
                    }
                    assignments.add(assignment);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn(e);
                    // Skip broken assignments -> Do not add them to list
                }
            }
        } catch (ApiException e) {
            LOGGER.warn("Could not query student management system for Assignments via \""
                + Settings.getConfig().getMgmtServerURL() + "\".", e);
        }
        
        return assignments;
    }
    
    /**
     * Loads the information of configured assignments from the <b>student management system</b>.
     * @param course Already known information of the course (will ignore all information regarding assignments). 
     * @return The configured assignments of the <b>student management system</b>.
     */
    public List<Assignment> loadAssignments(Course course) {
        // Try to use loaded students
        List<Member> studentsOfCourse = course.getStudents();
        if (null == studentsOfCourse) {
            /* 
             * Load all students from server if list is locally not available.
             * Tutors won't be touched /changed -> can be an empty group
             */
            studentsOfCourse = loadStudents(new Group());
        }
        
        List<Group> homeworkGroups = course.getHomeworkGroups();
        if (null == homeworkGroups) {
            homeworkGroups = loadGroups();
        }

        return loadAssignments(studentsOfCourse, homeworkGroups);
    }
    
    /**
     * Pulls the information of configured homework groups from the <b>student management system</b>.
     * @return The configured homework groups of the <b>student management system</b>.
     */
    public List<Group> loadGroups() {
        // Gather all homework groups
        List<Group> homeworkGroups = new ArrayList<>();
        try {
            List<GroupDto> groupsOfServer = groupsAPI.getGroupsOfCourse(courseID);
            for (GroupDto groupDto : groupsOfServer) {
                Group group = new Group();
                group.setGroupName(groupDto.getName());
                
                List<UserDto> userofGroup = groupsAPI.getUsersOfGroup(courseID, groupDto.getId());
                for (UserDto userDto : userofGroup) {
                    group.addMembers(userDto.getRzName());
                }
                homeworkGroups.add(group);
            }
        } catch (ApiException e) {
            LOGGER.warn("Could not query student management system for Groups via \""
                + Settings.getConfig().getMgmtServerURL() + "\".", e);
        }
        
        return homeworkGroups;
    }
    
    /**
     * Pulls the information of configured homework groups from the <b>student management system</b> for an assignment.
     * @param assignmentID The ID of the assignment.
     * @return The configured homework groups of the <b>student management system</b>.
     */
    public List<Group> loadGroupsAtSubmissionEnd(String assignmentID) {
        // Gather all homework groups for an assignment
        List<Group> homeworkGroups = new ArrayList<>();
        
        try {
            List<GroupDto> groupsOfServer = groupsAPI.getGroupsFromAssignment(courseID, assignmentID);
            for (GroupDto groupDto : groupsOfServer) {
                Group group = new Group();
                group.setGroupName(groupDto.getName());
                
                List<UserDto> userofGroup = groupsAPI.getUsersOfGroup(courseID, groupDto.getId());
                for (UserDto userDto : userofGroup) {
                    group.addMembers(userDto.getRzName());
                }
                homeworkGroups.add(group);
            }
        } catch (ApiException e) {
            LOGGER.warn("Could not query student management system for Groups via \""
                    + Settings.getConfig().getMgmtServerURL() + "\".", e);
        }
        
        return homeworkGroups;
    }
}
