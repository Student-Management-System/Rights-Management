package net.ssehub.rightsmanagement.logic;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.swagger.client.model.AssignmentDto.StateEnum;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.IParticipant;

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
        String userNameForTesting = "mmustermann";
        String assignmentNameForTesting = "Test_Assignment 01 (Java)";
        StateEnum expectedAssignmentState = StateEnum.IN_PROGRESS;
        int exptectedNoOfGroups = 2;
        int exptectedNoOfMembers = 2;
        int exptectedNoOfTutors = 0;
        int exptectedNoOfAssignments = 5;
        
        // Init and execute
        DataPullService connector = new DataPullService("http://147.172.178.30:3000", "java", "wise1920");
        Course course = connector.computeFullConfiguration();
        
        // Test the course
        Assertions.assertEquals(courseName, course.getCourseName());
        Assertions.assertEquals(semester, course.getSemester());
        
        // Test the tutors
        Group tutors = course.getTutors();
        Assertions.assertNotNull(tutors);
        Assertions.assertEquals(tutors.getMembers().size(), exptectedNoOfTutors);
        
        // Test homework groups
        List<Group> groups = course.getHomeworkGroups();
        Assertions.assertNotNull(groups);
        Assertions.assertFalse(groups.isEmpty(), "Course has no homework groups");
        Assertions.assertEquals(groups.size(), exptectedNoOfGroups);
        Group groupForTest = groups.stream()
            .filter(g -> groupNameForTesting.equals(g.getName()))
            .findAny()
            .orElse(null);
        Assertions.assertNotNull(groupForTest, "Expected group \""
            + groupNameForTesting + "\" not part of homework groups");
        Assertions.assertEquals(groupNameForTesting, groupForTest.getName());
        Assertions.assertEquals(exptectedNoOfMembers, groupForTest.getMembers().size());
        Assertions.assertTrue(groupForTest.getMembers().contains(userNameForTesting));
        
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
        // Expected assignment to be a group work
        for (IParticipant participant : assignmentForTest) {
            Assertions.assertSame(Group.class, participant.getClass(), "Group assignment containts individuals.");
        }
        Assertions.assertSame(expectedAssignmentState, assignmentForTest.getStatus());
    }

}
