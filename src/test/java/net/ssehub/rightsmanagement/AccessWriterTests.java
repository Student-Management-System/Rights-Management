package net.ssehub.rightsmanagement;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.User;
import net.ssehub.rightsmanagement.model.Course;

/**
 * This class contains the test cases that belongs to {@link AccessWriter}.
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
public class AccessWriterTests {
    
    private static final File TEST_FOLDER = new File(AllTests.TEST_FOLDER, "AccessWriter");

    /**
     * Reads the access file with the data to test.
     * @param fileName the name of the file that contains the test data.
     * @return a String with the test data that is read from the file.
     */
    private static String readAccessFile(String fileName) {
        String content = null;
        // Based on https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
        File path = new File(TEST_FOLDER, fileName);
        try {
            content = Files.readString(path.toPath()).trim();
            // next line need to be commented out under windows since lineSeperator has another behavior on windows, 
            // than under linux
            content = content.replace("\n", System.lineSeparator());
        } catch (IOException e) {
            Assertions.fail("Could not read configuration from " + path.getAbsolutePath(), e);
        }
        
        return content;
    }
    
    /**
     * Tests if one group with two members is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testNoAssignementsAndPermissionsAllRead() throws IOException { 
        // Create test data for writing
        Course course = new Course();
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("no_groups");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests that an empty group will be written correctly.
     * Incomplete group assignments or usage of undefined groups lead to broken repositories, which cannot accessed by
     * any user.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testEmptyTutorsGroup() throws IOException { 
        // Create test data for writing
        Course course = new Course();
        Group tutorGroup = new Group("JavaTutoren");
        course.setTutors(tutorGroup);
        Group group = getMemberGroup("JP0001");
        ManagedAssignment hw = new ManagedAssignment("In_Bearbeitung_Abgabe", null, State.SUBMISSION, true, 0);
        hw.addGroup(group);
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_emptyTutorsGroup");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests if the tutor group is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testTutorGroupWithMembersAndPermissionsAllRead() throws IOException {
        // Create test data for writing
        Course course = new Course();
        course.setTutors(getTutorGroup("JavaTutoren"));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_tutorGroup_access");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests if the tutor group is first and then the member groups is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testTutorGroup() throws IOException {     
        // Create test data for writing
        Course course = new Course();
        course.setTutors(getTutorGroup("JavaTutoren"));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_tutor_and_users_access");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests if the permissions for a invisible homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithInvisibleHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Course course = new Course();
        course.setTutors(getTutorGroup("JavaTutoren"));
        Group group = getMemberGroup("JP001");
        ManagedAssignment hw = new ManagedAssignment("Unsichtbare_Abgabe", null, State.INVISIBLE, true, 0);
        hw.addGroup(group);
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("permissions_invisibleAssignment");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests if the permissions for a in progress homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithInProgressHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Course course = new Course();
        course.setTutors(getTutorGroup("JavaTutoren"));
        Group group = getMemberGroup("JP001");
        ManagedAssignment hw = new ManagedAssignment("In_Bearbeitung_Abgabe", null, State.SUBMISSION, true, 0);
        hw.addGroup(group);
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("permissions_in_progressAssignment");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests if the permissions for a in review homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithInReviewHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Course course = new Course();
        course.setTutors(getTutorGroup("JavaTutoren"));
        Group group = getMemberGroup("JP001");
        ManagedAssignment hw = new ManagedAssignment("Unsichtbare_Abgabe", null, State.IN_REVIEW, true, 0);
        hw.addGroup(group);
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        // reuse permissions_invisibleAssignment because the rights should remain the same
        String expected = readAccessFile("permissions_invisibleAssignment");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests if the permissions for a evaluated homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithEvaluatedHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Course course = new Course();
        course.setTutors(getTutorGroup("JavaTutoren"));
        Group group = getMemberGroup("JP001");
        ManagedAssignment hw = new ManagedAssignment("Bewertet_Abgabe", null, State.REVIEWED, true, 0);
        hw.addGroup(group);
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "abgabe", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        // reuse permissions_invisibleAssignment because the rights should remain the same
        String expected = readAccessFile("permissions_evaluatedAssignment");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests rights assignment for a single assignment (no group work), <b>without</b> a tutor group.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsForSingleAssignmentWoTutors() throws IOException {
        // Create test data for writing
        Course course = new Course();
        ManagedAssignment hw = new ManagedAssignment("Exam", null, State.SUBMISSION, false, 0);
        hw.addGroup(Group.createSingleStudentGroup(new User("musterma", "musterma", "")));
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "submissionSystem", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("SingleAssignmentWoTutors_access");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests rights assignment for a single assignment (no group work), <b>with</b> a tutor group.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsForSingleAssignmentWithTutors() throws IOException {
        // Create test data for writing
        Course course = new Course();
        Group tutors = getTutorGroup("Tutors");
        course.setTutors(tutors);
        ManagedAssignment hw = new ManagedAssignment("Exam", null, State.REVIEWED, false, 0);
        hw.addGroup(Group.createSingleStudentGroup(new User("musterma", "musterma", "")));
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "submissionSystem", null);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("SingleAssignmentWithTutors_access");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    /**
     * Tests based on {@link #testPermissionsForSingleAssignmentWithTutors()} that blacklisted / deprecated folders
     * are handled corrently.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsForDeprecatedFolderWithTutors() throws IOException {
        // Create test data for writing
        Course course = new Course();
        Group tutors = getTutorGroup("Tutors");
        course.setTutors(tutors);
        ManagedAssignment hw = new ManagedAssignment("Exam", null, State.REVIEWED, false, 0);
        Group singleStudent = new Group("musterma");
        singleStudent.addMembers(new User("musterma", "musterma", ""));
        hw.addGroup(singleStudent);
        course.setAssignments(Arrays.asList(hw));
        
        // Simulate writing data to access file
        List<String> blacklistedFolders = new ArrayList<>();
        blacklistedFolders.add("DeprecatedAssignment");
        blacklistedFolders.add("RunningAssignment/DeprecatedGroup");
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.write(course, "submissionSystem", blacklistedFolders);
        aWriter.close();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("AccessForDeprecatedFolderWithTutors_access");
        Assertions.assertEquals(expected, sWriter.toString().trim());
    }
    
    // Create test data for writing -----------------------------------------------------------------------------------
    
    /**
     * Getter for the tutor group.
     * 
     * @param name The name of the tutor group.
     * 
     * @return the tutor group.
     */
    private Group getTutorGroup(String name) {
        Group tutorGroup = new Group(name);
        tutorGroup.addMembers(new User("tutor1", "tutor1", ""), new User("tutor2", "tutor2", ""));
        return tutorGroup;
    }
    
    /**
     * Getter for the member group.
     * @param name of the group.
     * @return the member group.
     */
    private Group getMemberGroup(String name) {
        Group memberGroup = new Group(name);
        memberGroup.addMembers(new User(name + "_user1", name + "_user1", ""),
            new User(name + "_user2", name + "_user2", ""));
        return memberGroup;
    }

}
