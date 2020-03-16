 package net.ssehub.rightsmanagement.svn;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.rightsmanagement.AllTests;
import net.ssehub.rightsmanagement.Unzipper;
import net.ssehub.rightsmanagement.model.Assignment;
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
     * @throws Exception
     */
    @Test
    public void testCreationOfSingleAssignment() throws Exception {
        repositoryTestFolder = Unzipper.unTarGz(new File(TEST_FOLDER, "EmptyRepository.tar.gz"));
        
        Repository repoWriter = new Repository(repositoryTestFolder.getAbsolutePath());
        Assignment assignment = new Assignment();
        assignment.setName("Exam");
        Member aStudent = new Member();
        aStudent.setMemberName("aStudent");
        assignment.addParticipant(aStudent);
        repoWriter.createOrModifyAssignment(assignment);
        
        Repository repoReader = new Repository(repositoryTestFolder.getAbsolutePath());
        Assertions.assertTrue(repoReader.pathExists("/" + assignment.getName() + "/" + aStudent.getName() + "/"));
    }
    
    /**
     * Cleans up after each test execution.
     */
    @AfterEach
    public void tearDown() {    
        try {
            FileUtils.deleteDirectory(repositoryTestFolder);
        } catch (IOException e) {
            Assertions.fail("Could not clean up " + repositoryTestFolder.getAbsolutePath() + " after test. "
                    + "Please delete this folder manually, otherwise next tests will fails.", e);
        }
    }
}
