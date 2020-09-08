package net.ssehub.rightsmanagement.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.Individual;
import net.ssehub.studentmgmt.backend_api.JSON;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

/**
 * {@link AbstractUpdateHandler} that stores the full configuration of a course locally and updates that configuration
 * based on incoming {@link NotificationDto}s.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class IncrementalUpdateHandler extends AbstractUpdateHandler {
    
    private static final Logger LOGGER = LogManager.getLogger(IncrementalUpdateHandler.class);
    
    private JSON parser = new JSON();
    private File cacheFile;

    /**
     * Creates a handler to manage updates for a course, which uses a local cache to reduce the total traffic.
     * @param courseConfig The configuration for the managed course.
     * @throws IOException If caching file does not exist and cannot be created.
     */
    public IncrementalUpdateHandler(CourseConfiguration courseConfig) throws IOException {
        super(courseConfig);
        init();
    }
    
    /**
     * Alternative constructor for testing.
     * @param courseConfig The configuration for the managed course.
     * @param connector The connector to use {@link DataPullService#DataPullService(CourseConfiguration)} or
     *      <tt>null</tt> during tests.
     * @throws IOException If caching file does not exist and cannot be created.
     */
    protected IncrementalUpdateHandler(CourseConfiguration courseConfig, DataPullService connector) throws IOException {
        super(courseConfig, connector);
        init();
    }
    
    /**
     * Loads the cached file at the constructor.
     * If the file does not exist, the information from the server is pulled to write the file with the current
     * configuration of the course.
     */
    private void init() {
        cacheFile = new File(Settings.getConfig().getCacheDir(), getCourseID() + ".json");
        if (!cacheFile.exists()) {
            LOGGER.info("{} does not exist, assuming new course. Creating the file for caching course information.",
                    cacheFile.getAbsolutePath());
            
            try (FileWriter writer = new FileWriter(cacheFile)) {
                // Pull initial configuration from server
                Course course = loadCourse();
                String content = parser.serialize(course);
                writer.write(content);
            } catch (IOException e) {
                LOGGER.warn("Could not create {}, cause {}", cacheFile.getAbsolutePath(), e);                
            }
        }
    }
    
    /**
     * Loads the full course configuration during initialization, may be overwritten for testing purpose.
     * @return The complete course information
     */
    protected Course loadCourse() {
        return getDataPullService().computeFullConfiguration();
    }

    @Override
    protected Course computeFullConfiguration(NotificationDto msg) {
        // First: Load cached state:
        Course course = getCachedState();
        
        // Second: Apply delta of the NotificationDto^
        // see https://github.com/Student-Management-System/StudentMgmt-Backend/blob/master/api-docs/docs/events.md
        switch (msg.getEvent()) {
        case ASSIGNMENT_CREATED:       // an assignment was created
        case ASSIGNMENT_STATE_CHANGED: // an assignment state was changed (e.g. submission phase -> review phase)
        case ASSIGNMENT_REMOVED:       // an assignment was deleted
        case REGISTRATIONS_CREATED:    // the initial set of groups for an assignment is created 
        case REGISTRATIONS_REMOVED:    // all groups for an assignment are removed
        case GROUP_REGISTERED:         // a new group is added to an assignment
        case GROUP_UNREGISTERED:       // a group is removed from an assignment
        case USER_REGISTERED:          // a user was added to a group of an assignment
        case USER_UNREGISTERED:        // a user was removed from a group of an assignment
            handleAssignmentChanged(course, msg.getAssignmentId());
            break;
        
        case USER_JOINED_GROUP:        // a user joined a group in the "global" group list of the course
        case USER_LEFT_GROUP:          // a user left a group in the "global" group list of the course
            // do nothing, as no running assignment is affected
            break;

        case COURSE_JOINED:            // some user has joined the course
            // update tutors
            Group tutors = getDataPullService().createTutorsGroup();
            course.setTutors(tutors);
            
            // update list of all participants
            List<Individual> studentsOfCourse = getDataPullService().loadStudents(tutors);
            course.setStudents(studentsOfCourse);
            
            // update all non-group assignments, as the list of students has changed
            for (Assignment assignment : course.getAssignments()) {
                if (!assignment.isGroupWork()) {
                    handleAssignmentChanged(course, assignment.getID());
                }
            }
            break;
            
        default:
            LOGGER.warn("{}s of type {} are not supported by {}", NotificationDto.class.getSimpleName(),
                msg.getEvent(), IncrementalUpdateHandler.class.getSimpleName());
            break;
        }
        
        return course;
    }
    
    /**
     * Handles a change in an {@link Assignment}. Tries to only reload this single assignment, but falls back if
     * the specified assignment ID cannot be found.
     * 
     * @param course The course that contains the assignment.
     * @param assignmentId The ID of the assignment that is changed. May be <code>null</code>.
     */
    private void handleAssignmentChanged(Course course, String assignmentId) {
        Assignment assignment = course.getAssignments().stream()
                .filter((a) -> a.getID().equals(assignmentId))
                .findAny()
                .orElse(null);
        
        if (assignmentId != null && assignment != null) {
            if (assignment.isGroupWork()) {
                assignment.setGroups(getDataPullService().loadGroupsPerAssignment(assignmentId));
            } else {
                assignment.setGroups(new LinkedList<>()); // clear previous groups
                course.getStudents().stream()
                    .map((student) -> Group.createSingleStudentGroup(student.getName()))
                    .forEach((singleStudentGroup) -> assignment.addGroup(singleStudentGroup));
            }
        } else {
            LOGGER.warn("Didn't find assignment for ID {}, reloading all assignments", assignmentId);
            course.setAssignments(getDataPullService().loadAssignments(course));
        }
    }
    
    /**
     * Loads the locally cached course information.
     * @return The locally saved {@link Course}.
     */
    private Course getCachedState() {
        Course result = null;
        try {
            String content = Files.readString(cacheFile.toPath());
            result = parser.deserialize(content, Course.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize cached course information", e);
            //TODO SE: throw exception to abort
        }
        return result;
    }

}
