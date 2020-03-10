package net.ssehub.rightsmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.AssignmentStates;
import net.ssehub.rightsmanagement.model.Group;

/**
 * This class contains the test cases that belongs to {@link AccessWriter}.
 * 
 * @author Kunold
 *
 */
public class AccessWriterTests {

    /**
     * Reads the access file with the data to test.
     * @param fileName the name of the file that contains the test data.
     * @return a String with the test data that is read from the file.
     */
    private static String readAccessFile(String fileName) {
        String contents = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(AllTests.TEST_FOLDER, fileName)))) {
            StringBuffer exptected = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                exptected.append(line);
                exptected.append('\n');
            }
            
            contents = exptected.toString();
        } catch (FileNotFoundException e) {
            Assertions.fail("Could not find access file for camparison: " + AllTests.TEST_FOLDER.getAbsolutePath() + "/"
                + fileName);
        } catch (IOException e) {
            Assertions.fail("Could not read comparison file " + fileName);
        }
        
        return contents;
    }
    
    /**
     * Tests if one group with two members is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testOneGroupWithTwoMembersAndPermissionsAllRead() throws IOException { 
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_oneGroup_access");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if two groups with two members is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testTwoGroupsWithTwoMembersAndPermissionsAllRead() throws IOException {        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.addGroup(getMemberGroup("JP002"));
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_twoGroups_access");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if the tutor group is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testTutorGroupWithMembersAndPermissionsAllRead() throws IOException {
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addTutorGroup(getTutorGroup());
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_tutorGroup_access");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if the tutor group is first and then the member groups is correctly written to the {@link AccessWriter}.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testTutorGroupAndMemberGroupsWithMembers() throws IOException {     
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addTutorGroup(getTutorGroup());
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.addGroup(getMemberGroup("JP002"));
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("groups_tutor_and_users_access");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if the permissions for a invisible homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithInvisibleHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Assignment hw = new Assignment();
        hw.setName("Unsichtbare_Abgabe");
        hw.setStatus(AssignmentStates.INVISIBLE);
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addTutorGroup(getTutorGroup());
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.addHomework(hw);
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("permissions_invisibleAssignment");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if the permissions for a in progress homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithInProgressHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Assignment hw = new Assignment();
        hw.setName("In_Bearbeitung_Abgabe");
        hw.setStatus(AssignmentStates.IN_PROGRESS);
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addTutorGroup(getTutorGroup());
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.addHomework(hw);
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        String expected = readAccessFile("permissions_in_progressAssignment");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if the permissions for a in review homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithInReviewHomeworkAndOneGroup() throws IOException {
        // Create test data for writing
        Assignment hw = new Assignment();
        // reuse Unsichtbare_Abgabe because the rights should remain the same
        hw.setName("Unsichtbare_Abgabe");
        hw.setStatus(AssignmentStates.IN_REVIEW);
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addTutorGroup(getTutorGroup());
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.addHomework(hw);
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        // reuse permissions_invisibleAssignment because the rights should remain the same
        String expected = readAccessFile("permissions_invisibleAssignment");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    /**
     * Tests if the permissions for a evaluated homework with one group is set right.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Test
    public void testPermissionsWithEvaluatedHomeworkAndOneGroup() throws IOException {
        
        Assignment hw = new Assignment();
        // reuse Unsichtbare_Abgabe because the rights should remain the same
        hw.setName("Bewertet_Abgabe");
        hw.setStatus(AssignmentStates.EVALUATED);
        
        // Simulate writing data to access file
        StringWriter sWriter = new StringWriter();
        AccessWriter aWriter = new AccessWriter(sWriter);
        aWriter.addTutorGroup(getTutorGroup());
        aWriter.addGroup(getMemberGroup("JP001"));
        aWriter.addHomework(hw);
        aWriter.write();
        
        // Compare expected and actual output of aWriter
        // reuse permissions_invisibleAssignment because the rights should remain the same
        String expected = readAccessFile("permissions_evaluatedAssignment");
        Assertions.assertEquals(expected, sWriter.toString());
    }
    
    // Create test data for writing -----------------------------------------------------------------------------------
    
    /**
     * Getter for the tutor group.
     * @return the tutor group.
     */
    private Group getTutorGroup() {
        Group tutorGroup = new Group();
        tutorGroup.setGroupName("JavaTutoren");
        tutorGroup.addMembers("tutor1", "tutor2");
        return tutorGroup;
    }
    
    /**
     * Getter for the member group.
     * @param name of the group.
     * @return the member group.
     */
    private Group getMemberGroup(String name) {
        Group memberGroup = new Group();
        memberGroup.setGroupName(name);
        memberGroup.addMembers(name + "_user1", name + "_user2");
        return memberGroup;
    }

}
