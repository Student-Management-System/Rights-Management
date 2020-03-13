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
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;

/**
 * {@link AbstractUpdateHandler} that stores the full configuration of a course locally and updates that configuration
 * based on incoming {@link UpdateMessage}s.
 * @author El-Sharkawy
 *
 */
public class IncrementalUpdateHandler extends AbstractUpdateHandler {
    
    private static final Logger LOGGER = Log.getLog();
    
    private JSON parser = new JSON();
    private File cacheFile;

    /**
     * Creates a handler to manage updates for a course, which uses a local cache to reduce the total traffic.
     * @param CourseConfiguration The configuration for the managed course.
     * @throws IOException If caching file does not exist and cannot be created.
     */
    public IncrementalUpdateHandler(CourseConfiguration courseConfig) throws IOException {
        super(courseConfig);
        cacheFile = new File(Settings.getConfig().getCacheDir(), getCourseID() + ".json");
        if (!cacheFile.exists()) {
            LOGGER.info("{} does not exist, assuming new course. Creating the file for caching course information.",
                cacheFile.getAbsolutePath());
            
            try (FileWriter writer = new FileWriter(cacheFile)) {
                // Pull initial configuration from server
                DataPullService connector = new DataPullService(courseConfig);
                Course course = connector.computeFullConfiguration();
                String content = parser.serialize(course);
                writer.write(content);
            } catch (IOException e) {
                LOGGER.warn("Could not create {}, cause {}", cacheFile.getAbsolutePath(), e);                
            }
        }
    }

    @Override
    protected Course computeFullConfiguration(UpdateMessage msg) {
        // First: Load cached state:
        Course course = getCachedState();
        
        // Second: Apply delta of the UpdateMessage
        // TODO SE: missing
        switch (msg.getAffectedObject()) {
        case GROUP:
            List<Group> groups = course.getHomeworkGroups();
            switch (msg.getType()) {
            case INSERT:
                // TODO SE: API missing
            }
            break;
        }
        
        return course;
    }
    
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