package net.ssehub.rightsmanagement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.rightsmanagement.TestUtils;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.Individual;

/**
 * Tests the {@link DataPullService}.<p>
 * <font color="red"><b>Warning:</b></font> This is an integration tests, that strongly depends on the test data send
 * by the selected <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class DataPullServiceTest {
    
    /**
     * Tests that the complete and correct information of a course is pulled from the student management system. 
     */
    @Test
    public void testComputeFullConfiguration() {
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
        TestUtils.loginViaVmArgs();
        DataPullService connector = new DataPullService("http://147.172.178.30:3000", "java", "wise1920");
        Course course = connector.computeFullConfiguration();
        
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
        Assertions.assertTrue(tutors.getMembers().contains(new Individual(tutorNameForTesting)),
                "Expected tutor " + tutorNameForTesting + " not part of tutors");
        
        // Test assignments
        List<Assignment> assignments = course.getAssignments();
        Assertions.assertNotNull(assignments);
        Assertions.assertFalse(assignments.isEmpty(), "Course has no assignments");
        Assertions.assertEquals(assignments.size(), exptectedNoOfAssignments);
        Assignment assignmentForTest = assignments.stream()
            .filter(a -> assignmentNameForTesting.equals(a.getName()))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(assignmentForTest, "Expected assignment \""
            + assignmentNameForTesting + "\" not part of assignments");
        Assertions.assertEquals(assignmentNameForTesting, assignmentForTest.getName());
        Assertions.assertSame(expectedAssignmentState, assignmentForTest.getState());
        
        // Test group in assignment
        Assignment assignment = assignments.get(2); // TODO: which assignment to use?
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
