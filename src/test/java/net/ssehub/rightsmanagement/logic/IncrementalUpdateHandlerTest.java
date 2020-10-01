package net.ssehub.rightsmanagement.logic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.RightsManagementProtocol;
import net.ssehub.rightsmanagement.TestUtils;
import net.ssehub.rightsmanagement.UpdateMessageLoader;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

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
    
    /**
     * Tests register of a new Group for a group assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testGroupRegisteredGroupAssignment() throws NetworkException {
        // Must be a valid name w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 01 (Java)", 
                "b2f6c008-b9f7-477f-9e8b-ff34ce339077", expectedState, true, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Precondition: Group should not be part
        Assertions.assertEquals(0, cachedState.getAssignments().get(0).getAllGroupNames().length);
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GROUP_REGISTERED_GROUP");
        NotificationDto updateMsg = UpdateMessageLoader.load("GROUP_REGISTERED_GROUP.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        
        // Post condition: Group should be added
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }
    
    /**
     * Tests register of a new Group for a single assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testGroupRegisteredSingleAssignment() throws NetworkException {
        // Must be a valid name w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("elshar", "hpeter", "kunold", "mmustermann"));
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 06 (Java) Testat In Progress", 
                "5b69db81-edbd-4f73-8928-1450036a75cb", expectedState, false, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Precondition: Group should not be part
        Assertions.assertEquals(0, cachedState.getAssignments().get(0).getAllGroupNames().length);
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GROUP_REGISTERED_SINGLE");
        NotificationDto updateMsg = UpdateMessageLoader.load("GROUP_REGISTERED_SINGLE.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        
        // Post condition: Group should be added
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }
    
    /**
     * Tests register of a new Group for a group assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testGroupRegisteredNoAssignmentId() throws NetworkException {
       // Must be a valid name w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 01 (Java)", 
                "wrong_id", expectedState, true, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Precondition: Group should not be part
        Assertions.assertEquals(0, cachedState.getAssignments().get(0).getAllGroupNames().length);
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GROUP_REGISTERED_GROUP");
        NotificationDto updateMsg = UpdateMessageLoader.load("GROUP_REGISTERED_GROUP.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        
        // Post condition: Group should be added
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }

    
    /**
     * Tests unregister of a Group.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testGroupUnregister() throws NetworkException {
        // Must be a valid name w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        int nGroupsBeforeDelte = 4;
        initEmptyCourse();
        Group g1 = new Group("Testgroup 1");
        Group g2 = new Group("Testgroup 2");
        Group g3 = new Group("Testgroup 3");
        Group g4 = new Group("Testgroup 4");
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 01 (Java)", 
                "wrong_id", State.SUBMISSION, true, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        cachedState.getAssignments().get(0).setGroups(Arrays.asList(g1, g2, g3, g4));
        
        // Precondition: Group should contain four groups
        Assertions.assertEquals(nGroupsBeforeDelte, cachedState.getAssignments().get(0).getAllGroupNames().length);
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_GROUP_UNREGISTERED");
        NotificationDto updateMsg = UpdateMessageLoader.load("GROUP_UNREGISTERED.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Group 4 should be removed
        Assertions.assertEquals(nGroupsBeforeDelte - 1, 
                changedCourse.getAssignments().get(0).getAllGroupNames().length);
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }
    
//    /**
//     * Tests user joins a group.
//     */
//    @Test
//    public void testUserJoinGroup() {
//       // Must be a valid name w.r.t the ID of the Notification
//        Individual expectedUser = new Individual("mmustermann");
//        initEmptyCourse();
//        
//        // Precondition: Group should not be part
//        Assertions.assertTrue(cachedState.getHomeworkGroups().isEmpty());
//        
//        // Apply update
//        IncrementalUpdateHandler handler = loadHandler("test_USER_JOINED_GROUP");
//        NotificationDto updateMsg = UpdateMessageLoader.load("USER_JOINED_GROUP.json");
//        Course changedCourse = handler.computeFullConfiguration(updateMsg);
//        
//        // Temporary fix "Homework groups do not longer contain user"
//        Group homeworkGroup = new Group("New Group");
//        homeworkGroup.addMembers(expectedUser);
//        changedCourse.setHomeworkGroups(Arrays.asList(homeworkGroup));
//        
//        // Post condition: Group should be added
//        Group newUserGroupRelation = changedCourse.getHomeworkGroups().stream()
//            .filter(g -> g.getMembers().contains(expectedUser))
//            .findAny()
//            .orElse(null);
//        Assertions.assertNotNull(newUserGroupRelation, "Specified user-group-relation not added. Either algorithm is "
//                + "broken or test data has changed.");
//    }
//    
//    /**
//     * Tests user left group.
//     */
//    @Test
//    public void testUserLeftGroup() {
//       // Must be a valid name w.r.t the ID of the Notification
//        Individual expectedUser = new Individual("Peter Pan");
//        int nGroups = 3;
//        initEmptyCourse();
//        Group g1 = new Group("Testgroup 1");
//        Group g2 = new Group("Testgroup 2");
//        g2.addMembers(expectedUser);
//        Group g3 = new Group("Testgroup 3");
//        cachedState.setHomeworkGroups(Arrays.asList(g1, g2, g3));
//        
//        // Precondition: Group should contain two groups
//        Assertions.assertEquals(nGroups, cachedState.getHomeworkGroups().size());
//        Assertions.assertFalse(cachedState.getHomeworkGroups().isEmpty());
//        
//        // Apply update
//        IncrementalUpdateHandler handler = loadHandler("test_USER_LEFT_GROUP");
//        NotificationDto updateMsg = UpdateMessageLoader.load("USER_LEFT_GROUP.json");
//        Course changedCourse = handler.computeFullConfiguration(updateMsg);
//        
//        // Post condition: User of Group 2 should be removed
//        Assertions.assertEquals(nGroups, changedCourse.getHomeworkGroups().size());
//        Group changedGroup = changedCourse.getHomeworkGroups().stream()
//            .filter(g -> g.getName().equals(g2.getName()))
//            .findAny()
//            .orElse(null);
//        Assertions.assertNotNull(changedGroup, "Group wasn't updated, but removed (not desired).");
//        Assertions.assertFalse(changedGroup.getMembers().contains(expectedUser), "Expected user not deleted");
//    }
    
    /**
     * Tests creating a new single assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testAssignmentCreatedSingleAssignment() throws NetworkException {
       // Must be a valid assignmentname w.r.t the ID of the Notification
        String expectedAssignmentName = "Test_Assignment 06 (Java) Testat In Progress";
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        
        // Precondition: Assignment should not be part
        Assertions.assertTrue(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_ASSIGNMENT_CREATED_SINGLE");
        NotificationDto updateMsg = UpdateMessageLoader.load("ASSIGNMENT_CREATED_SINGLE.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment should be added
        Assertions.assertFalse(changedCourse.getAssignments().isEmpty());
        ManagedAssignment actual = changedCourse.getAssignments().get(1);
        Assignment newAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(expectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(newAssignment, "Specified assignment not added. Either algorithm is broken "
            + "or test data has changed.");
        Assertions.assertEquals(expectedState, actual.getState());
        Assertions.assertFalse(actual.isGroupWork());
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }
    
    /**
     * Tests creation a new group assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testAssignmentCreatedGroupAssignment() throws NetworkException {
        // Must be a valid assignmentname w.r.t the ID of the Notification
        String expectedAssignmentName = "Test_Assignment 01 (Java)";
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        
        // Precondition: Assignment should not be part
        Assertions.assertTrue(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_ASSIGNMENT_CREATED_GROUP");
        NotificationDto updateMsg = UpdateMessageLoader.load("ASSIGNMENT_CREATED_GROUP.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment should be added
        Assertions.assertFalse(changedCourse.getAssignments().isEmpty());
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        Assignment newAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(expectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(newAssignment, "Specified assignment not added. Either algorithm is broken "
            + "or test data has changed.");
        Assertions.assertEquals(expectedState, actual.getState());
        Assertions.assertTrue(actual.isGroupWork());
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }

    /**
     * Tests update a group assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testAssignmentStateChangedGroupAssignment() throws NetworkException {
        // Must be a valid name w.r.t the ID of the Notification
        String assignmentName = "Test_Assignment 01 (Java)";
        State changedState = State.IN_REVIEW;
        
        initEmptyCourse();
        ManagedAssignment assignmentPreUpdate = new ManagedAssignment(assignmentName,
                "b2f6c008-b9f7-477f-9e8b-ff34ce339077", changedState, true, 0);
        cachedState.setAssignments(Arrays.asList(assignmentPreUpdate));
        
        // Precondition: Assignment should be part
        Assertions.assertFalse(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_ASSIGNMENT_STATE_CHANGED_GROUP");
        NotificationDto updateMsg = UpdateMessageLoader.load("ASSIGNMENT_STATE_CHANGED_GROUP.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment should be updated
        Assertions.assertFalse(changedCourse.getAssignments().isEmpty());
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        Assignment updatedAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(assignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(updatedAssignment, "Specified assignment not updated");
        Assertions.assertEquals(changedState, actual.getState());
        Assertions.assertTrue(actual.isGroupWork());
        
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
        
        // undo state change
        ManagedAssignment assignmentAfterTest = new ManagedAssignment(assignmentName,
                "b2f6c008-b9f7-477f-9e8b-ff34ce339077", State.SUBMISSION, false, 0);
        cachedState.setAssignments(Arrays.asList(assignmentAfterTest));        
        updateMsg = UpdateMessageLoader.load("ASSIGNMENT_STATE_CHANGED_SINGLE.json");
        changedCourse = handler.computeFullConfiguration(updateMsg);
        actual = changedCourse.getAssignments().get(0);
        Assertions.assertEquals(State.SUBMISSION, actual.getState());
    }
    
    /**
     * Tests update a single assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testAssignmentStateChangedSingleAssignment() throws NetworkException {
        // Must be a valid name w.r.t the ID of the Notification
        String assignmentName = "Test_Assignment 06 (Java) Testat In Progress";
        State changedState = State.IN_REVIEW;
        
        initEmptyCourse();
        ManagedAssignment assignment = new ManagedAssignment(assignmentName,
                "5b69db81-edbd-4f73-8928-1450036a75cb", changedState, false, 0);
        cachedState.setAssignments(Arrays.asList(assignment));
        
        // Precondition: Assignment should be part
        Assertions.assertFalse(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_ASSIGNMENT_STATE_CHANGED_SINGLE");
        NotificationDto updateMsg = UpdateMessageLoader.load("ASSIGNMENT_STATE_CHANGED_SINGLE.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment should be updated
        Assertions.assertFalse(changedCourse.getAssignments().isEmpty());
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        Assignment updatedAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(assignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(updatedAssignment, "Specified assignment not updated");
        Assertions.assertEquals(changedState, actual.getState());
        Assertions.assertFalse(actual.isGroupWork());
        
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
        
        // undo state change
        ManagedAssignment assignmentAfterTest = new ManagedAssignment(assignmentName,
                "5b69db81-edbd-4f73-8928-1450036a75cb", State.SUBMISSION, false, 0);
        cachedState.setAssignments(Arrays.asList(assignmentAfterTest));        
        updateMsg = UpdateMessageLoader.load("ASSIGNMENT_STATE_CHANGED_SINGLE.json");
        changedCourse = handler.computeFullConfiguration(updateMsg);
        actual = changedCourse.getAssignments().get(0);
        Assertions.assertEquals(State.SUBMISSION, actual.getState());
    }
    
    /**
     * Tests removing of a single Assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testAssignmentRemovedSingleAssignment() throws NetworkException {
       // Must be a valid name w.r.t the ID of the Notification
        String expectedAssignmentName = "Test_Assignment 09";
        initEmptyCourse();
        
        ManagedAssignment assignment = new ManagedAssignment(expectedAssignmentName,
                "not-existing-id", State.INVISIBLE, false, 0);
        cachedState.setAssignments(Arrays.asList(assignment));
        
        // Precondition: Assignment should contain six assignments
        Assertions.assertFalse(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_ASSIGNMENT_REMOVED");
        NotificationDto updateMsg = UpdateMessageLoader.load("ASSIGNMENT_REMOVED.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment six should be removed
        Assignment removedAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(expectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNull(removedAssignment, "Specified assignment not removed");
        
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }
    
    /**
     * Tests removing of a group Assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testAssignmentRemovedGroupAssignment() throws NetworkException {
       // Must be a valid name w.r.t the ID of the Notification
        String expectedAssignmentName = "Test_Assignment 09";
        initEmptyCourse();
        
        ManagedAssignment assignment = new ManagedAssignment(expectedAssignmentName,
                "not-existing-id", State.INVISIBLE, true, 0);
        cachedState.setAssignments(Arrays.asList(assignment));
        
        // Precondition: Assignment should contain six assignments
        Assertions.assertFalse(cachedState.getAssignments().isEmpty());
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_ASSIGNMENT_REMOVED");
        NotificationDto updateMsg = UpdateMessageLoader.load("ASSIGNMENT_REMOVED.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: Assignment six should be removed
        Assignment removedAssignment = changedCourse.getAssignments().stream()
            .filter(a -> a.getName().contains(expectedAssignmentName))
            .findAny()
            .orElse(null);
        Assertions.assertNull(removedAssignment, "Specified assignment not removed");
        
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
    }
    
    /**
     * Tests adding of a user to a group of a group assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testUserRegisteredGroupAssignment() throws NetworkException {
        // Must be a valid groupname w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 01 (Java)", 
                "b2f6c008-b9f7-477f-9e8b-ff34ce339077", expectedState, true, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_USER_REGISTERED_GROUP");
        NotificationDto updateMsg = UpdateMessageLoader.load("USER_REGISTERED_GROUP.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: User should be added to group
        Assertions.assertEquals(1, changedCourse.getAssignments().size(), 
                "unexpectedly assignments where created or deleted");
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        Assertions.assertEquals(expectedGroupNames.size(), actual.getAllGroupNames().length);
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
        Assertions.assertEquals(expectedState, actual.getState());
    }
    
    /**
     * Tests adding of a user to a group of a single assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testUserRegisteredSingleAssignment() throws NetworkException {
        // Must be a valid username w.r.t the ID of the Notification
        Set<String> expectedUserNames = new HashSet<>(Arrays.asList("elshar", "hpeter", "kunold", "mmustermann"));
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 06 (Java) Testat In Progress", 
                "5b69db81-edbd-4f73-8928-1450036a75cb", expectedState, false, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_USER_REGISTERED_SINGLE");
        NotificationDto updateMsg = UpdateMessageLoader.load("USER_REGISTERED_SINGLE.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: User should be added to assignment
        Assertions.assertEquals(1, changedCourse.getAssignments().size(), 
                "unexpectedly assignments where created or deleted");
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        Assertions.assertEquals(expectedUserNames.size(), actual.getAllGroupNames().length);
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedUserNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
        Assertions.assertEquals(expectedState, actual.getState());
    }
    
    /**
     * Tests adding of a user to a group of a group assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testUserRegisteredNoAssignmentID() throws NetworkException {
        // Must be a valid groupname w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        State expectedState = State.SUBMISSION;
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 01 (Java)", 
                "wrong_id", expectedState, true, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_USER_REGISTERED_GROUP");
        NotificationDto updateMsg = UpdateMessageLoader.load("USER_REGISTERED_GROUP.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: User should be added to group
        Assertions.assertEquals(6, changedCourse.getAssignments().size(), 
                "unexpectedly assignments where created or deleted");
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        Assertions.assertEquals(expectedGroupNames.size(), actual.getAllGroupNames().length);
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
        Assertions.assertEquals(expectedState, actual.getState());
    }
    
    /**
     * Tests removing a user from a group of an assignment.
     * @throws NetworkException when network problems occur.
     */
    @Test
    public void testCourseUserRelationRemove() throws NetworkException {
        // Must be a valid groupname w.r.t the ID of the Notification
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        State expectedState = State.SUBMISSION;
        
        initEmptyCourse();
        ManagedAssignment ma = new ManagedAssignment("Test_Assignment 01 (Java)", 
                "b2f6c008-b9f7-477f-9e8b-ff34ce339077", expectedState, true, 0);
        cachedState.setAssignments(Arrays.asList(ma));
        
        // Apply update
        IncrementalUpdateHandler handler = loadHandler("test_USER_UNREGISTERED");
        NotificationDto updateMsg = UpdateMessageLoader.load("USER_UNREGISTERED.json");
        Course changedCourse = handler.computeFullConfiguration(updateMsg);
        
        // Post condition: User should be removed from group
        Assertions.assertEquals(6, changedCourse.getAssignments().size(), 
                "unexpectedly assignments where created or deleted");
        ManagedAssignment actual = changedCourse.getAssignments().get(0);
        
        Assertions.assertEquals(expectedGroupNames.size(), actual.getAllGroupNames().length);
        String[] actualGroups = actual.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + actual.getName() + "', but not expected.");
        }
        
        // tutors and users should not be affected
        Assertions.assertNull(cachedState.getTutors());
        Assertions.assertTrue(cachedState.getStudents().isEmpty());
        Assertions.assertEquals(expectedState, actual.getState());
    }
    
    /**
     * Creates a basis {@link Course} object for the tests.
     * This can be accessed via {@link #cachedState}.
     */
    private void initEmptyCourse() {
        cachedState = new Course();
        cachedState.setCourseName(COURSE_NAME_FOR_TESTING);
        cachedState.setSemester(SEMESTER_FOR_TESTING);
    }
    
    /**
     * Loads and prepares the {@link IncrementalUpdateHandler} for testing.
     * @param testName The name of the test, will be used to create separate, temporary test folders for each test.
     * @return The {@link IncrementalUpdateHandler} for testing.
     * @throws NetworkException when network problems occur.
     */
    private IncrementalUpdateHandler loadHandler(String testName) throws NetworkException {
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
         * @throws NetworkException when network problems occur.
         */
        public HandlerForTesting(CourseConfiguration courseConfig) throws IOException, NetworkException {
            super(courseConfig, new RightsManagementProtocol("http://147.172.178.30:8080", "http://147.172.178.30:3000",
                    COURSE_NAME_FOR_TESTING, SEMESTER_FOR_TESTING));
            // Login in through credentials provided via JVM args
            String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
            try {
                getDataPullService().login(credentials[0], credentials[1]);
            } catch (UnknownCredentialsException | ServerNotFoundException e) {
                Assertions.fail("Could not login for testing due to: " + e.getMessage(), e);
            }

        }
        
        @Override
        protected Course getCachedState() {
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
