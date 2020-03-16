 package net.ssehub.rightsmanagement.svn;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tmatesoft.svn.core.SVNException;

import net.ssehub.rightsmanagement.AllTests;
import net.ssehub.rightsmanagement.Unzipper;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.Member;

/**
 * Tests the {@link Repository}.
 * @author El-Sharkawy
 *
 */
public class RepositoryTest {
    private static final File TEST_FOLDER = new File(AllTests.TEST_FOLDER, "Repository");
    private File repositoryTestFolder;
    
    /**
     * Tests:
     * <ul>
     *   <li><b>Creation</b> of a new assignment</li>
     *   <li><b>Creation</b> a submission folder for a <b>user</b></li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testCreationOfSingleAssignment() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "EmptyRepository.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath());
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath());
        Assignment assignment = new Assignment();
        assignment.setName("Exam");
        Member aStudent = new Member();
        aStudent.setMemberName("aStudent");
        assignment.addParticipant(aStudent);
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + aStudent.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (Exception e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + aStudent.getName() + "/"));
    }
    
    /**
     * Tests:
     * <ul>
     *   <li><b>Creation</b> of a new assignment</li>
     *   <li><b>Creation</b> a submission folder for a <b>group</b></li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testCreationOfGroupAssignment() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "EmptyRepository.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath());
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath());
        Assignment assignment = new Assignment();
        assignment.setName("Homework");
        Group group = new Group();
        group.setGroupName("group1");
        group.addMembers("student1", "student2");
        assignment.addParticipant(group);
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + group.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (Exception e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group.getName() + "/"));
    }
    
    /**
     * Tests:
     * <ul>
     *   <li><b>Modification</b> of an existent assignment</li>
     *   <li><b>Keeping</b> a submission folder for an existing group</li>
     *   <li><b>Creation</b> a submission folder for a new <b>group</b></li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testModificationOfGroupAssignment() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "Repository_with_one_Assignment.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath());
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath());
        Assignment assignment = new Assignment();
        assignment.setName("Homework");
        Group group1 = new Group();
        group1.setGroupName("group1");
        group1.addMembers("student1", "student2");
        Group group2 = new Group();
        group2.setGroupName("group2");
        group2.addMembers("student3", "student4");
        assignment.addAllParticipants(Arrays.asList(group1, group2));
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group1.getName() + "/"));
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + group2.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (Exception e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group1.getName() + "/"));
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group2.getName() + "/"));
    }
    
    /**
     * Cleans up after each test execution.
     */
    @AfterEach
    public void tearDown() {    
        if (repositoryTestFolder.exists()) {
            try {
                FileUtils.deleteDirectory(repositoryTestFolder);
            } catch (IOException e) {
                Assertions.fail("Could not clean up " + repositoryTestFolder.getAbsolutePath() + " after test. "
                        + "Please delete this folder manually, otherwise next tests will fails.", e);
            }
        }
    }
}
