package net.ssehub.rightsmanagement.logic;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.RightsManagementProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.User;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Course;

/**
 * Pulls information from the <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class DataPullService {
    
    private String courseName;
    private String semester;
//    private String courseID;
//    private String tutorsGroupName;
//    
//    private AssignmentRegistrationApi apiAssignmentRegistrations;
//    private AssignmentsApi assignmentsAPI;
//    private CourseParticipantsApi courseParticipantsAPI;
    private RightsManagementProtocol protocol;
    
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
        protocol = new RightsManagementProtocol(null, serverURL, courseName, null);
//        ApiClient client = new ApiClient().setBasePath(serverURL);
        if (null != Settings.INSTANCE.getLogin()) {
            protocol.setAccessToken(Settings.INSTANCE.getLogin().getManagementToken());
//            client.setAccessToken(Settings.INSTANCE.getLogin().getManagementToken());
        }
        protocol.setSemester(semester);
        
//        assignmentsAPI = new AssignmentsApi(client);
//        courseParticipantsAPI = new CourseParticipantsApi(client);
//        apiAssignmentRegistrations = new AssignmentRegistrationApi(client);
//        
        this.courseName = courseName;
        this.semester = semester;
//        this.courseID = courseName + "-" + semester;
    }

    /**
     * Pulls the complete course information.
     * @return The complete course information
     */
    public Course computeFullConfiguration() throws NetworkException {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setSemester(semester);
        
        Group tutors = protocol.getTutors();
        List<User> studentsOfCourse = protocol.getStudents();
        course.setTutors(tutors);
        course.setStudents(studentsOfCourse);
        
        // Collect assignments
        List<ManagedAssignment> assignments = protocol.loadAssignments(studentsOfCourse);
        course.setAssignments(assignments);
        
        return course;
    }
    
    /**
     * Creates an empty group to store the tutors of the course.
     * This method ensures that the Tutors get a unique / protected group name, that should not be used from
     * any homework group.
     * @return A new, empty group that is intended to store tutors.
     * @throws NetworkException If network problems occur
     */
    public Group createTutorsGroup() throws NetworkException {
        return protocol.getTutors();
    }

    /**
     * Loads the list of known students from the course.
     * @return The list of participating students
     * @throws NetworkException If network problems occur
     */
    public List<User> loadStudents() throws NetworkException {
        return protocol.getStudents();
    }
    
    /**
     * Pulls the information of configured {@link ManagedAssignment}s from the <b>student management system</b>.
     * @param studentsOfCourse A cached list of known participants of the course, used to reduce traffic.
     *     Maybe <tt>null</tt>, in this case the list is pulled from the server.
     * @return The assignments of the course, containing the participants of the assignments (students in case of
     *     single assignments, otherwise the groups).
     * @throws NetworkException If network problems occur
     */
    public List<ManagedAssignment> getAssignmnets(List<User> studentsOfCourse) throws NetworkException {
        return protocol.loadAssignments(studentsOfCourse);
    }
//        List<Individual> studentsOfCourse = new ArrayList<Individual>();
//        try {
//            List<ParticipantDto> usersOfCourse =
//                    courseParticipantsAPI.getUsersOfCourse(courseID, null, null, null, null);
//            for (ParticipantDto userDto : usersOfCourse) {
//                switch (userDto.getRole()) {
//                case STUDENT:
//                    Individual student = new Individual(userDto.getUsername());
//                    studentsOfCourse.add(student);
//                    break;
//                case LECTURER:
//                    // falls through
//                case TUTOR:
//                    tutors.addMembers(new Individual(userDto.getUsername()));
//                    break;
//                default:
//                    LOGGER.warn("{} is an administrator and user of the course {}. Cannot handle this user.",
//                        userDto.getUsername(), courseID);
//                    break;
//                }
//            }
//        } catch (ApiException e) {
//            LOGGER.warn("Could not query student management system for Users via \""
//                + Settings.getConfig().getMgmtServerURL() + "\", cause: " + e.getCode() + " - " + e.getResponseBody(),
//                e);
//        }
//        return studentsOfCourse;
//    }
//
//    /**
//     * Pulls the information of configured {@link Assignment}s from the <b>student management system</b>.
//     * @param studentsOfCourse The list of known participants of the course.
//     * @return The assignments of the course, containing the participants of the assignments (students in case of
//     *     single assignments, otherwise the groups).
//     */
//    private List<Assignment> loadAssignments(Collection<Individual> studentsOfCourse) {
//        List<Assignment> assignments = new ArrayList<>();
//        try {
//            List<AssignmentDto> assignmentsOfServer = assignmentsAPI.getAssignmentsOfCourse(courseID);
//            for (AssignmentDto assignmentDto : assignmentsOfServer) {
//                try {
//                    Assignment assignment = new Assignment(assignmentDto);
//                    if (assignment.isGroupWork()) {
//                        List<Group> homeworkGroups = loadGroupsPerAssignment(assignment.getID());
//                        assignment.addAllGroups(homeworkGroups);   
//                    } else {
//                        studentsOfCourse.stream()
//                            .map((student) -> Group.createSingleStudentGroup(student.getName()))
//                            .forEach((singleStudentGroup) -> assignment.addGroup(singleStudentGroup));
//                    }
//                    assignments.add(assignment);
//                } catch (IllegalArgumentException e) {
//                    LOGGER.warn(e);
//                    // Skip broken assignments -> Do not add them to list
//                }
//            }
//        } catch (ApiException e) {
//            LOGGER.warn("Could not query student management system for Assignments via \""
//                + Settings.getConfig().getMgmtServerURL() + "\", cause: " + e.getCode() + " - " + e.getResponseBody(),
//                e);
//        }
//        
//        return assignments;
//    }
//    
//    /**
//     * Loads the information of configured assignments from the <b>student management system</b>.
//     * @param course Already known information of the course (will ignore all information regarding assignments). 
//     * @return The configured assignments of the <b>student management system</b>.
//     */
//    public List<Assignment> loadAssignments(Course course) {
//        // Try to use loaded students
//        List<Individual> studentsOfCourse = course.getStudents();
//        if (null == studentsOfCourse) {
//            /* 
//             * Load all students from server if list is locally not available.
//             * Tutors won't be touched /changed -> can be an empty group
//             */
//            studentsOfCourse = loadStudents(new Group("empty tutors"));
//        }
//        
//        return loadAssignments(studentsOfCourse);
//    }
//    
//    /**
//     * Pulls the information of configured homework groups from the <b>student management system</b> for an 
//     * assignment.
//     * @param assignmentID The ID of the assignment.
//     * @return The configured homework groups of the <b>student management system</b>.
//     */
//    public List<Group> loadGroupsPerAssignment(String assignmentID) {
//        // Gather all homework groups for an assignment
//        List<Group> homeworkGroups = new ArrayList<>();
//        
//        try {
//            List<GroupDto> groupsOfServer = apiAssignmentRegistrations.getRegisteredGroups(courseID, assignmentID,
//                null, null, null);
//            for (GroupDto groupDto : groupsOfServer) {
//                Group group = new Group(groupDto.getName());
//                
//                for (ParticipantDto userDto : groupDto.getMembers()) {
//                    group.addMembers(new Individual(userDto.getUsername()));
//                }
//                homeworkGroups.add(group);
//            }
//        } catch (ApiException e) {
//            LOGGER.warn("Could not query student management system for Groups via \""
//                + Settings.getConfig().getMgmtServerURL() + "\", cause: " + e.getCode() + " - " + e.getResponseBody(),
//                e);
//        }
//        
//        return homeworkGroups;
//    }
    
}
