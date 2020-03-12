package net.ssehub.rightsmanagement.logic;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.swagger.client.JSON;
import io.swagger.client.model.AssignmentDto.StateEnum;
import net.ssehub.rightsmanagement.AccessWriter;
import net.ssehub.rightsmanagement.conf.Configuration;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.IParticipant;

/**
 * Tests the {@link RestUpdateHandler}.<p>
 * <font color="red"><b>Warning:</b></font> This is an integration tests, that strongly depends on the test data send
 * by the selected <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class RestUpdateHandlerTest {
    
    static {
        // Specifies the student management system to use during testing.
        Configuration config = new Configuration();
        config.setMgmtURL("http://147.172.178.30:3000");
        Settings.INSTANCE.loadConfig(new JSON().serialize(config));
    }
    
    /**
     * A {@link RestUpdateHandler} which will be used during the tests here.
     * It uses the the logic of the parent class, but won't write changes to disk.
     * @author El-Sharkawy
     *
     */
    private static class HandlerForTesting extends RestUpdateHandler {

        private StringWriter sWriter;
        
        public HandlerForTesting(CourseConfiguration courseConfig) {
            super(courseConfig);
            sWriter = new StringWriter();
        }
        
        @Override
        protected void updateRepository(Course course) throws IOException {
            // Avoid writing to disk during test -> Not needed
        }
        
        protected AccessWriter createWriter() throws IOException {
            // Avoid writing to disk during test -> Use StringWriter
            return new AccessWriter(sWriter);            
        }
    }
    
    /**
     * Creates a configuration for the test.
     * @param courseName The name of the course as used by the management system.
     * @param semester The name of the course as used by the management system.
     * @return The configuration to use in a test.
     */
    private static CourseConfiguration createConfig(String courseName, String semester) {
        CourseConfiguration config = new CourseConfiguration();
        config.setCourseName(courseName);
        config.setSemester(semester);
        return config;
    }
    
    /**
     * Tests that the complete and correct information of a course is pulled from the student management system. 
     */
    @Test
    public void testComputeFullConfiguration() {
        // Values of the student management system used for the test
        String courseName = "java";
        String semester = "wise1920";
        String groupNameForTesting = "Testgroup 1";
        String userNameForTesting = "a019ea22-5194-4b83-8d31-0de0dc9bca53";
        String assignmentNameForTesting = "Test_Assignment 01 (Java)";
        StateEnum expectedAssignmentState = StateEnum.IN_PROGRESS;
        int exptectedNoOfGroups = 2;
        int exptectedNoOfMembers = 2;
        int exptectedNoOfAssignments = 3;
        
        // Init and execute
        CourseConfiguration config = createConfig("java", "wise1920");
        HandlerForTesting handler = new HandlerForTesting(config);
        Course course = handler.computeFullConfiguration(null);
        
        // Test the course
        Assertions.assertEquals(courseName, course.getCourseName());
        Assertions.assertEquals(semester, course.getSemester());
        
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
