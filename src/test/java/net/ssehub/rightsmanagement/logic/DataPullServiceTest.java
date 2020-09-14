package net.ssehub.rightsmanagement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.RightsManagementProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.User;
import net.ssehub.rightsmanagement.TestUtils;
import net.ssehub.rightsmanagement.model.Course;

/**
 * Tests the {@link RightsManagementProtocol}.<p>
 * This test is deprecated and should be removed <font color="red"><b>after</b></font> {@link RightsManagementProtocol}
 * is completely tested at the Submitter-Protocol.
 * @author El-Sharkawy
 *
 */
public class DataPullServiceTest {
    
    /**
     * Loads the information from the server for testing.
     * @param connector The protocol to use
     * @param courseName The name of the course to use
     * @param semester the semester to use for the test
     * @return The information to configure the submission repository
     * @throws NetworkException If network problems occur
     */
    private Course computeFullConfiguration(RightsManagementProtocol connector, String courseName, String semester)
        throws NetworkException {
        
        Course course = new Course();
        course.setCourseName(courseName);
        course.setSemester(semester);
        
        // update tutors
        Group tutors = connector.getTutors();
        course.setTutors(tutors);
        
        // update list of all participants
        List<User> studentsOfCourse = connector.getStudents();
        course.setStudents(studentsOfCourse);
        
        // update all non-group assignments, as the list of students has changed
        List<ManagedAssignment> assignments = connector.loadAssignments(studentsOfCourse);
        course.setAssignments(assignments);
        
        return course;
    }
    
    /**
     * Tests that the complete and correct information of a course is pulled from the student management system. 
     */
    @Test
    public void testComputeFullConfiguration() throws NetworkException {
        // Values of the student management system used for the test
        String courseName = "java";
        String semester = "wise1920";
        String groupNameForTesting = "Testgroup 1";
        String tutorNameForTesting = "jdoe";
        String assignmentNameForTesting = "Test_Assignment 01 (Java)";
        State expectedAssignmentState = State.SUBMISSION;
        int expectedNoOfGroups = 3;
//        int exptectedNoOfMembers = 2;
        int exptectedNoOfTutors = 3;
        int exptectedNoOfAssignments = 6;
        
        // Init and execute
        RightsManagementProtocol connector = new RightsManagementProtocol("http://147.172.178.30:8080",
            "http://147.172.178.30:3000", "java", semester);
        String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
        connector.login(credentials[0], credentials[1]);
        Course course = computeFullConfiguration(connector, courseName, semester);
        
        // Test the course
        Assertions.assertEquals(courseName, course.getCourseName());
        Assertions.assertEquals(semester, course.getSemester());
        
        // Test the tutors
        Group tutors = course.getTutors();
        Assertions.assertNotNull(tutors);
        String expectedTutorsGroupName = "Tutors_of_Course_"
            + courseName.substring(0, 1).toUpperCase() + courseName.substring(1);
        Assertions.assertEquals(expectedTutorsGroupName, tutors.getName());
        Assertions.assertEquals(exptectedNoOfTutors, tutors.getMembers().size());
        Assertions.assertTrue(tutors.getMembers().contains(new User(tutorNameForTesting, tutorNameForTesting, "")),
                "Expected tutor " + tutorNameForTesting + " not part of tutors");
        
        // Test assignments
        List<ManagedAssignment> assignments = course.getAssignments();
        Assertions.assertNotNull(assignments);
        Assertions.assertFalse(assignments.isEmpty(), "Course has no assignments");
        Assertions.assertEquals(assignments.size(), exptectedNoOfAssignments);
        ManagedAssignment assignmentForTest = assignments.stream()
            .filter(a -> assignmentNameForTesting.equals(a.getName()))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(assignmentForTest, "Expected assignment \""
            + assignmentNameForTesting + "\" not part of assignments");
        Assertions.assertEquals(assignmentNameForTesting, assignmentForTest.getName());
        Assertions.assertSame(expectedAssignmentState, assignmentForTest.getState());
        
        // Test group in assignment
        ManagedAssignment assignment = assignments.get(2); // TODO: which assignment to use?
        assertEquals(expectedNoOfGroups, assignment.getAllGroupNames().length);
        Group groupForTest = StreamSupport.stream(assignment.spliterator(), false)
                .filter(g -> groupNameForTesting.equals(g.getName()))
                .findAny()
                .orElse(null);
        Assertions.assertNotNull(groupForTest, "Expected group \""
                + groupNameForTesting + "\" not part of homework groups");
        Assertions.assertEquals(groupNameForTesting, groupForTest.getName());
    }
    
}
