package net.ssehub.rightsmanagement.svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
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
     * Tests that a new repository can be created on demand.
     * @throws RepositoryNotFoundException In case that the repo
     */
    @Test
    public void testInitRepository() {
        // Create empty target folder
        repositoryTestFolder = new File(TEST_FOLDER, "repo");
        Assertions.assertFalse(repositoryTestFolder.exists());
        Assertions.assertTrue(repositoryTestFolder.mkdirs());
        repositoryTestFolder.deleteOnExit();
        
        // Init repository
        try {
            new Repository(repositoryTestFolder.getAbsolutePath(), "a_user", true);
        } catch (RepositoryNotFoundException e) {
            Assertions.fail("Could not create repository at: " + repositoryTestFolder.getAbsolutePath(), e);
        }
        
        // Check if a repository is created at the specified location. This should contain some elements according to
        // http://svnbook.red-bean.com/en/1.7/svn.reposadmin.basics.html
        String[] children = repositoryTestFolder.list();
        Assertions.assertNotNull(children);
        Set<String> files = new HashSet<>(Arrays.asList(children));
        Assertions.assertTrue(files.contains("conf"));
        Assertions.assertTrue(files.contains("db"));
        Assertions.assertTrue(files.contains("format"));
        Assertions.assertTrue(files.contains("hooks"));
        Assertions.assertTrue(files.contains("locks"));
        Assertions.assertTrue(files.contains("README.txt"));
    }
    
    /**
     * Tests that a new repository won't be created, if not requested.
     * @throws RepositoryNotFoundException 
     */
    @Test
    public void testNoInitRepository() throws RepositoryNotFoundException {
        // Create empty target folder
        repositoryTestFolder = new File(TEST_FOLDER, "repo");
        Assertions.assertFalse(repositoryTestFolder.exists());
        Assertions.assertTrue(repositoryTestFolder.mkdirs());
        repositoryTestFolder.deleteOnExit();
        
        // Init repository
        new Repository(repositoryTestFolder.getAbsolutePath(), "a_user", false);
        
        // Check if a repository is created at the specified location. This should contain some elements according to
        // http://svnbook.red-bean.com/en/1.7/svn.reposadmin.basics.html
        String[] children = repositoryTestFolder.list();
        Assertions.assertTrue(children.length == 0, "Error: There was something unexpected created at "
            + repositoryTestFolder.getAbsolutePath());
    }
    
    /**
     * Tests.
     * <ul>
     *   <li><b>Creation</b> of a new assignment</li>
     *   <li><b>Creation</b> of a submission folder for a <b>user</b></li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testCreationOfSingleAssignment() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "EmptyRepository.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        Assignment assignment = new Assignment("Exam", null, State.SUBMISSION, false);
        Member aStudent = new Member();
        aStudent.setMemberName("aStudent");
        assignment.addParticipant(aStudent);
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + aStudent.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + aStudent.getName() + "/"));
    }
    
    /**
     * Tests.
     * <ul>
     *   <li><b>Creation</b> of a new assignment</li>
     *   <li><b>Creation</b> of a submission folder for a <b>group</b></li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testCreationOfGroupAssignment() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "EmptyRepository.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        Assignment assignment = new Assignment("Homework", null, State.SUBMISSION, true);
        Group group = new Group();
        group.setGroupName("group1");
        group.addMembers("student1", "student2");
        assignment.addParticipant(group);
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + group.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group.getName() + "/"));
    }
    
    /**
     * Tests.
     * <ul>
     *   <li><b>Modification</b> of an existent assignment</li>
     *   <li><b>Keeping</b> a submission folder for an existing group</li>
     *   <li><b>Creation</b> of a submission folder for a new <b>group</b></li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testModificationOfGroupAssignment() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "Repository_with_one_Assignment.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        Assignment assignment = new Assignment("Homework", null, State.SUBMISSION, true);
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
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group1.getName() + "/"));
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + group2.getName() + "/"));
    }
    
    /**
     * Tests.
     * <ul>
     *   <li><b>Modification</b> of an existent assignment</li>
     *   <li><b>Missing groups</b> to create</li>
     * </ul>
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException In case of {@link Repository#pathExists(String)} is broken (which is used to validate the
     *     results).
     */
    @Test
    public void testCreationOfAssignmentWithEmptyGroup() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "Repository_with_one_Assignment.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        long oldRevision = repoReader.lastRevision();
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        Assignment assignment = new Assignment("Homework", null, State.SUBMISSION, true);
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        long newRevision = repoReader.lastRevision(); 
        Assertions.assertSame(oldRevision, newRevision, "Repository was altered altough there was no data to write");
    }
    
    /**
     * Tests if the author is written to the repository.
     */
    @Disabled
    @Test
    public void testWriteAuthor() throws RepositoryNotFoundException, SVNException {
        File file = new File(TEST_FOLDER, "Repository_with_one_Assignment.tar.gz");
        repositoryTestFolder = Unzipper.unTarGz(file);
        //Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        Assignment assignment = new Assignment("Homework", null, State.SUBMISSION, true);
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        //Map fileProperties = new HashMap();
        SVNProperties fileProperties = new SVNProperties();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            SVNRepository repo = SVNRepositoryFactory.create(SVNURL.fromFile(file));
            repo.getFile(repositoryTestFolder.getAbsolutePath(), -1,  fileProperties, baos);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        System.out.println(fileProperties);
        
        //Assertions.assertEquals(expected, actual);
        
        
    }
    
    /**
     * Tests that constructor throws an exception if a location is specified which does not exist.
     */
    @Test
    public void testRepositoryWithoutExistingLocalRepository() {
        File notExisting = new File("a_not_existing_file");
        
        // Test precondition: File does not exist
        Assertions.assertFalse(notExisting.exists());
        
        // Test that exception is thrown
        Exception exception = Assertions.assertThrows(RepositoryNotFoundException.class, 
            () -> new Repository(notExisting.getAbsolutePath(), "test", false));
        Assertions.assertTrue(exception.getMessage().contains("repository location"));
    }
    
    /**
     * Tests that constructor throws an exception if a location is specified that is not a directory.
     */
    @Test
    public void testRepositoryPointingToFile() {
        File testFile = new File(TEST_FOLDER, "EmptyRepository.tar.gz");
        
        // Test precondition: File exists but is not a directory
        Assertions.assertTrue(testFile.exists());
        Assertions.assertTrue(testFile.isFile());
        
        // Test that exception is thrown
        Exception exception = Assertions.assertThrows(RepositoryNotFoundException.class, 
            () -> new Repository(testFile.getAbsolutePath(), "test", false));
        Assertions.assertTrue(exception.getMessage().contains("repository directory"));
    }
    
    /**
     * Cleans up after each test execution.
     */
    @AfterEach
    public void tearDown() {    
        if (null != repositoryTestFolder && repositoryTestFolder.exists()) {
            try {
                FileUtils.deleteDirectory(repositoryTestFolder);
            } catch (IOException e) {
                Assertions.fail("Could not clean up " + repositoryTestFolder.getAbsolutePath() + " after test. "
                        + "Please delete this folder manually, otherwise next tests will fails.", e);
            }
        }
    }
}
