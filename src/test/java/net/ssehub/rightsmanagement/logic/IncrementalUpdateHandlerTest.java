package net.ssehub.rightsmanagement.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.UpdateMessageLoader;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;

/**
 * Tests the {@link IncrementalUpdateHandler}.
 * @author El-Sharkawy
 *
 */
public class IncrementalUpdateHandlerTest {
    
    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String COURSE_NAME_FOR_TESTING = "java";
    private static final String SEMESTER_FOR_TESTING = "wise1920";
    
    private static File cacheFolder;
    private static Course cachedState;
    
    static {
        if (null == Settings.getConfig()) {
            try {
                // Create a basis configuration to avoid NullPointers, which may be changed during tests
                Settings.INSTANCE.init();
            } catch (IOException e) {
                Assertions.fail("Could not initialize the configuration", e);
            }            
        }
    }
    
    /**
     * Tests insertion of a new Group.
     */
    @Test
    public void testGroupInsert() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String expectedGroupName = "Testgroup 1";
        initEmptyCourse();
        
        // Precondition: Group should not be part
        Assertions.assertTrue(cachedState.getHomeworkGroups().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GroupInsert");
        UpdateMessage updateMsg = UpdateMessageLoader.load("GroupInsert.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Group should be added
        Assertions.assertFalse(changedCourse.getHomeworkGroups().isEmpty());
        Group newGroup = changedCourse.getHomeworkGroups().stream()
            .filter(g -> g.getName().contains(expectedGroupName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(newGroup, "Specified group not added. Either algorithm is broken "
            + "or test data has changed.");
    }
    
    /**
     * Creates a basis {@link Course} object for the tests.
     * This can be accessed via {@link #cachedState}.
     */
    private void initEmptyCourse() {
        cachedState = new Course();
        cachedState.setCourseName(COURSE_NAME_FOR_TESTING);
        cachedState.setCourseName(SEMESTER_FOR_TESTING);
        cachedState.setHomeworkGroups(new ArrayList<Group>());
        cachedState.setAssignments(new ArrayList<Assignment>());
    }
    
    /**
     * Loads and prepares the {@link IncrementalUpdateHandler} for testing.
     * @param testName The name of the test, will be used to create separate, temporary test folders for each test.
     * @return The {@link IncrementalUpdateHandler} for testing.
     */
    private IncrementalUpdateHandler loadHandler(String testName) {
        cacheFolder = new File(TEMP_DIR, testName);
        cacheFolder.mkdir();
        Settings.getConfig().setCacheDir(cacheFolder.getAbsolutePath());
        
        CourseConfiguration config = new CourseConfiguration();
        config.setCourseName(COURSE_NAME_FOR_TESTING);
        config.setSemester(SEMESTER_FOR_TESTING);
        
        IncrementalUpdateHandler testHandler = null;
        try {
            testHandler = new HandlerForTesting(config);
        } catch (IOException e) {
            Assertions.fail("Could not create handler for testing", e);
        }
        
        return testHandler;
    }
    
    /**
     * Handler class for testing, will use {@link IncrementalUpdateHandlerTest#cachedState} as basis for applying
     * changes instead of loading the current course set-up from a server.
     * @author El-Sharkawy
     *
     */
    private class HandlerForTesting extends IncrementalUpdateHandler {

        /**
         * Creates a {@link IncrementalUpdateHandler} instance, that can be used for testing and does not write to
         * the local disk.
         * @param courseConfig The configuration for the managed course.
     * @throws IOException If caching file does not exist and cannot be created.
         */
        public HandlerForTesting(CourseConfiguration courseConfig) throws IOException {
            super(courseConfig, new DataPullService("http://147.172.178.30:3000", COURSE_NAME_FOR_TESTING,
                SEMESTER_FOR_TESTING));
        }
        
        @Override
        protected Course loadCourse() {
            // Course needs to be available before constructor runs -> load it via test class and not as a parameter
            return IncrementalUpdateHandlerTest.cachedState;
        }
        
    }
    
    /**
     * Removes temporarily created cache after test execution.
     */
    @AfterEach
    public void tearDown() {
        if (null != cacheFolder && cacheFolder.exists()) {
            try {
                FileUtils.deleteDirectory(cacheFolder);
            } catch (IOException e) {
                Assertions.fail("Could not clean up " + cacheFolder.getAbsolutePath() + " after test. "
                        + "Please delete this folder manually, otherwise next tests will fails.", e);
            }
        }
    }

}
