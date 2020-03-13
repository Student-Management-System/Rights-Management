package net.ssehub.rightsmanagement.logic;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.AccessWriter;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.model.Course;

/**
 * Tests the {@link RestUpdateHandler}.
 * @author El-Sharkawy
 *
 */
public class RestUpdateHandlerTest {
    
    private static final CourseConfiguration CONFIG;
    
    static {
        CourseConfiguration config = new CourseConfiguration();
        config.setCourseName("java");
        config.setSemester("wise1920");
        CONFIG = config;
    }
    
    /**
     * A {@link RestUpdateHandler} which will be used during the tests here.
     * It uses the the logic of the parent class, but won't write changes to disk.
     * @author El-Sharkawy
     *
     */
    private static class HandlerForTesting extends RestUpdateHandler {

        private StringWriter sWriter;
        private Course courseForTesting;
        
        public HandlerForTesting(CourseConfiguration config, Course course) {
            // DataPullService not used in test -> set it to null to avoid rest calls
            super(config, null);
            courseForTesting = course;
            sWriter = new StringWriter();
        }
        
        /**
         * Returns the content which will be written by the {@link RestUpdateHandler} into the access file.
         * Will be available after calling the {@link #update(UpdateMessage)} method.
         * @return The written content to the simulated access file.
         */
        public String getAccessContent() {
            return sWriter.toString();
        }
        
        @Override
        protected Course computeFullConfiguration(UpdateMessage msg) {
            return courseForTesting;
        }
        
        @Override
        protected void updateRepository(Course course) throws IOException {
            // Avoid writing to disk during test -> Not needed
        }
        
        @Override
        protected AccessWriter createWriter() throws IOException {
            // Avoid writing to disk during test -> Use StringWriter
            return new AccessWriter(sWriter);            
        }
    }
    
    /**
     * Tests that the complete and correct information of a course is pulled from the student management system. 
     * @throws IOException Not expected during test
     */
    @Test
    public void testComputeFullConfiguration() throws IOException {
        Course courseForTesting = new Course();
        HandlerForTesting handler = new HandlerForTesting(CONFIG, courseForTesting);
        handler.update(null);
        String content = handler.getAccessContent();
        
        System.out.println(content);
    }

}
