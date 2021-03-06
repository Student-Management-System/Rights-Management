package net.ssehub.rightsmanagement.svn;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.User;
import net.ssehub.rightsmanagement.AllTests;
import net.ssehub.rightsmanagement.Unzipper;
import net.ssehub.rightsmanagement.model.Course;

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
     * Tests the retrieval of folders via {@link Repository#listFolders()}.
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     */
    @Test
    public void testListFolders() throws RepositoryNotFoundException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "Repository_with_one_Assignment.tar.gz"));
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        
        Set<String> folders = repoReader.listFolders();
        Assertions.assertEquals(2, folders.size());
        Assertions.assertTrue(folders.contains("Homework"));
        Assertions.assertTrue(folders.contains("Homework/group1"));
    }
    
    /**
     * Tests the retrieval of folders via {@link Repository#updateRepository(net.ssehub.rightsmanagement.model.Course)}.
     * @throws RepositoryNotFoundException If the unpacked test repository could not be found
     * @throws SVNException 
     */
    @Test
    public void testListFoldersOfUpdateRepository() throws RepositoryNotFoundException, SVNException {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "Repository_with_one_Assignment.tar.gz"));
        Repository repo = new Repository(repositoryTestFolder.getAbsolutePath(), "test", false);
        
        Set<String> folders = repo.updateRepository(new Course());
        // Similar as in testListFolders(), but must not contain sub folder
        Assertions.assertEquals(1, folders.size());
        Assertions.assertTrue(folders.contains("Homework"));
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
        ManagedAssignment assignment = new ManagedAssignment("Exam", null, State.SUBMISSION, false, 0);
        Group singleStudent = Group.createSingleStudentGroup(new User("aStudent", "aStudent", ""));
        assignment.addGroup(singleStudent);
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + singleStudent.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + singleStudent.getName() + "/"));
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
        ManagedAssignment assignment = new ManagedAssignment("Homework", null, State.SUBMISSION, true, 0);
        Group singleStudent = Group.createSingleStudentGroup(new User("student2", "student2", ""));
        assignment.addGroup(singleStudent);
        Assertions.assertFalse(repoReader.pathExists("/" + assignment.getName() + "/" + singleStudent.getName() + "/"));
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + singleStudent.getName() + "/"));
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
        ManagedAssignment assignment = new ManagedAssignment("Homework", null, State.SUBMISSION, true, 0);
        Group group1 = new Group("group1");
        group1.addMembers(new User("student1", "student1", ""), new User("student2", "student2", ""));
        Group group2 = new Group("group2");
        group2.addMembers(new User("student3", "student3", ""), new User("student4", "student4", ""));
        assignment.addAllGroups(Arrays.asList(group1, group2));
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
        ManagedAssignment assignment = new ManagedAssignment("Homework", null, State.SUBMISSION, true, 0);
        
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
    @Test
    public void testWriteAuthor() throws RepositoryNotFoundException, SVNException {
        String expectedAuthor = "Test Author of testWriteAuthor()";
        File file = new File(TEST_FOLDER, "EmptyRepository.tar.gz");
        repositoryTestFolder = Unzipper.unTarGz(file);
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath(), expectedAuthor, false);
        ManagedAssignment assignment = new ManagedAssignment("Homework", null, State.SUBMISSION, true, 0);
        
        // Check that repository is empty
        SVNRepository repoReader = SVNRepositoryFactory.create(SVNURL.fromFile(repositoryTestFolder.getAbsoluteFile()));
        Collection<SVNDirEntry> entries = repoReader.getDir("/", -1, null, SVNDirEntry.DIRENT_ALL,
                (Collection<?>) null);
        Assertions.assertTrue(entries.isEmpty());
        
        // Write changes to repository
        try {
            repoWriter.createOrModifyAssignment(assignment);
        } catch (SVNException e) {
            Assertions.fail("Could not create assignment " + assignment.getName() + " which was explicitly testet.", e);
        }
        
        // Check that some entries exist and all of them are created by expected author
        entries = repoReader.getDir("/", -1, null, SVNDirEntry.DIRENT_ALL, (Collection<?>) null);
        int nEntries = 0;
        for (SVNDirEntry svnDirEntry : entries) {
            Assertions.assertEquals(expectedAuthor, svnDirEntry.getAuthor(),
                    "Newly created items are created by wrong author");
            nEntries++;
        }
        Assertions.assertTrue(nEntries > 0, "There was no entry (file/folder) created."); 
        
        repoReader.closeSession();
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
