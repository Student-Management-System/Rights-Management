package net.ssehub.rightsmanagement.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import io.swagger.client.JSON;
import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.Member;

/**
 * {@link AbstractUpdateHandler} that stores the full configuration of a course locally and updates that configuration
 * based on incoming {@link UpdateMessage}s.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class IncrementalUpdateHandler extends AbstractUpdateHandler {
    
    private static final Logger LOGGER = Log.getLog();
    
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
    protected Course computeFullConfiguration(UpdateMessage msg) {
        // First: Load cached state:
        Course course = getCachedState();
        
        // Second: Apply delta of the UpdateMessage
        switch (msg.getAffectedObject()) {
        case GROUP:
            /*
             * TODO SE: Consider to load only the specified group. Currently, all groups are loaded since there
             * is no other API available.
             */
            List<Group> groups = getDataPullService().loadGroups();
            course.setHomeworkGroups(groups);
            break;
        case ASSIGNMENT:
            // TODO SE: Consider to load only the specified assignment. Currently, all assignment.
            List<Assignment> assignments = getDataPullService().loadAssignments(course);
            course.setAssignments(assignments);
            break;
        case USER_GROUP_RELATION:
            List<Group> userGroupRelation = getDataPullService().loadGroups();
            course.setHomeworkGroups(userGroupRelation);
            break;
        case COURSE_USER_RELATION:
            // falls through
        case USER:
            Group tutors = new Group();
            List<Member> studentsOfCourse = getDataPullService().loadStudents(tutors);
            course.setStudents(studentsOfCourse);
            course.setTutors(tutors);
            break;
        default:
            LOGGER.warn("{}s of type {} are not supported by {}", UpdateMessage.class.getSimpleName(),
                msg.getAffectedObject(), IncrementalUpdateHandler.class.getSimpleName());
            break;
        }
        
        return course;
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
            LOGGER.warn("Could not deserialize cachec course information", e);
            //TODO SE: throw exception to abort
        }
        return result;
    }

}
