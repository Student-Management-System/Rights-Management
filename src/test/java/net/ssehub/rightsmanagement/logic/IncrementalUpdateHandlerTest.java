package net.ssehub.rightsmanagement.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
 * @author Kunold
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
     * Tests update a Group.
     */
    @Test
    public void testGroupUpdate() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String notExpectedGroupName = "Testgruppe 5";
        initEmptyCourse();
        Group group = new Group();
        group.setGroupName("Testgruppe 5");
        cachedState.setHomeworkGroups(Arrays.asList(group));
        
        // Precondition: Group should be part
        Assertions.assertFalse(cachedState.getHomeworkGroups().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GroupUpdate");
        UpdateMessage updateMsg = UpdateMessageLoader.load("GroupUpdate.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Group should be updated
        Assertions.assertFalse(changedCourse.getHomeworkGroups().isEmpty());
        Group updatedGroup = changedCourse.getHomeworkGroups().stream()
            .filter(g -> !g.getName().contains(notExpectedGroupName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(updatedGroup, "Specified group not updated");
    }
    
    /**
     * Tests removing a Group.
     */
    @Test
    public void testGroupRemove() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String expectedGroupName = "Testgroup 3";
        int nGroupsBeforeDelte = 3;
        initEmptyCourse();
        Group g1 = new Group();
        g1.setGroupName("Testgroup 1");
        Group g2 = new Group();
        g2.setGroupName("Testgroup 2");
        Group g3 = new Group();
        g3.setGroupName("Testgroup 3");
        cachedState.setHomeworkGroups(Arrays.asList(g1, g2, g3));
        
        // Precondition: Group should contain three groups
        Assertions.assertEquals(nGroupsBeforeDelte, cachedState.getHomeworkGroups().size());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GroupRemove");
        UpdateMessage updateMsg = UpdateMessageLoader.load("GroupRemove.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Group 3 should be removed
        Assertions.assertEquals(nGroupsBeforeDelte - 1, changedCourse.getHomeworkGroups().size());
        Group removedGroup = changedCourse.getHomeworkGroups().stream()
            .filter(g -> g.getName().contains(expectedGroupName))
            .findAny()
            .orElse(null);
        Assertions.assertNull(removedGroup, "Specified group not removed.");
    }
    
    /**
     * Tests insertion of a new User-Group-Relation.
     */
    @Test
    public void testUserGroupRelationInsert() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String expectedUserName = "mmustermann";
        initEmptyCourse();
        
        // Precondition: Group should not be part
        Assertions.assertTrue(cachedState.getHomeworkGroups().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_UserGroupRelationInsert");
        UpdateMessage updateMsg = UpdateMessageLoader.load("UserGroupRelationInsert.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Group should be added
        Assertions.assertFalse(changedCourse.getHomeworkGroups().isEmpty());
        Group newUserGroupRelation = changedCourse.getHomeworkGroups().stream()
            .filter(g -> g.getMembers().contains(expectedUserName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(newUserGroupRelation, "Specified user-group-relation not added. Either algorithm is "
                + "broken or test data has changed.");
    }
    
    /**
     * Tests update a User-Group-Relation.
     */
    @Test
    public void testUserGroupRelationUpdate() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String notExpectedUserName = "Peter Pan";
        initEmptyCourse();
        Group group = new Group();
        group.setGroupName("Testgroup 1");
        group.addMembers("Peter Pan");
        cachedState.setHomeworkGroups(Arrays.asList(group));
        
        // Precondition: Group should be part
        Assertions.assertFalse(cachedState.getHomeworkGroups().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_UserGroupRelationUpdate");
        UpdateMessage updateMsg = UpdateMessageLoader.load("UserGroupRelationUpdate.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Group should be updated
        Assertions.assertFalse(changedCourse.getHomeworkGroups().isEmpty());
        Group updatedUserGroupRelation = changedCourse.getHomeworkGroups().stream()
            .filter(g -> !g.getMembers().contains(notExpectedUserName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(updatedUserGroupRelation, "Specified user group relation not updated");
    }
    
    /**
     * Tests insertion of a new Assignment.
     */
    @Test
    public void testAssignmentInsert() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String expectedAssignmentName = "Test_Assignment 01 (Java)";
        initEmptyCourse();
        
        // Precondition: Assignment should not be part
        Assertions.assertTrue(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_AssignmentInsert");
        UpdateMessage updateMsg = UpdateMessageLoader.load("AssignmentInsert.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment should be added
        Assertions.assertFalse(changedCourse.getAssignments().isEmpty());
        Assignment newAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(expectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(newAssignment, "Specified assignment not added. Either algorithm is broken "
            + "or test data has changed.");
    }
    
    /**
     * Tests update an Assignment.
     */
    @Test
    public void testAssignmentUpdate() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String notExpectedAssignmentName = "Test Assignment";
        
        initEmptyCourse();
        Assignment assignment = new Assignment();
        assignment.setName("Test Assignment");
        cachedState.setAssignments(Arrays.asList(assignment));
        
        // Precondition: Assignment should be part
        Assertions.assertFalse(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_AssignmentUpdate");
        UpdateMessage updateMsg = UpdateMessageLoader.load("AssignmentUpdate.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment should be updated
        Assertions.assertFalse(changedCourse.getAssignments().isEmpty());
        Assignment updatedAssignment = changedCourse.getAssignments().stream()
            .filter(a -> !a.getName().contains(notExpectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(updatedAssignment, "Specified assignment not updated");
    }
    
    /**
     * Tests removing of an Assignment.
     */
    @Test
    public void testAssignmentRemove() {
       // Must be a valid name w.r.t the ID of the UpdateMessage
        String expectedAssignmentName = "Test_Assignment 06";
        int nAssignmentsBeforeDelte = 6;
        initEmptyCourse();
        Assignment assignment1 = new Assignment();
        assignment1.setName("Test_Assignment 01 (Java)");
        Assignment assignment2 = new Assignment();
        assignment2.setName("Test_Assignment 02 (Java)");
        Assignment assignment3 = new Assignment();
        assignment3.setName("Test_Assignment 03 (Java)");
        Assignment assignment4 = new Assignment();
        assignment4.setName("Test_Assignment 04 (Java)");
        Assignment assignment5 = new Assignment();
        assignment5.setName("Test_Assignment 05 (Java) Invisible");
        Assignment assignment6 = new Assignment();
        assignment6.setName("Test_Assignment 06");
        cachedState.setAssignments(Arrays.asList(assignment1, assignment2, assignment3, assignment4, assignment5,
                assignment6));
        
        // Precondition: Assignment should contain six assignments
        Assertions.assertEquals(nAssignmentsBeforeDelte, cachedState.getAssignments().size());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_AssignmentRemove");
        UpdateMessage updateMsg = UpdateMessageLoader.load("AssignmentRemove.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment six should be removed
        Assertions.assertEquals(nAssignmentsBeforeDelte - 1, changedCourse.getAssignments().size());
        Assignment removedAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(expectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(removedAssignment, "Specified assignment not removed");
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
