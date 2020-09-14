package net.ssehub.rightsmanagement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import net.ssehub.exercisesubmitter.protocol.backend.LoginComponent;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;

/**
 * Provides constants and utility functions used by multiple tests.
 * @author El-Sharkawy
 *
 */
public class TestUtils {
    
    // Test parameters
    public static final String TEST_AUTH_SERVER = "http://147.172.178.30:8080";
    public static final String TEST_MANAGEMENT_SERVER = "http://147.172.178.30:3000";
    public static final String TEST_SUBMISSION_SERVER = "http://svn.submission.fake/not_existing_submissions";
    
    /**
     * The default java course used for integration tests.
     * Other course related information stored in this class are related to this course.
     */
    public static final String TEST_DEFAULT_JAVA_COURSE = "java";
    public static final String TEST_DEFAULT_SEMESTER = "wise1920";
    public static final String TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE = "993b3cd0-6207-11ea-bc55-0242ac130003";
    public static final String TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP = "f50b8474-1fb9-4d69-85a2-76648d0fd3f9";

    /**
     * Tests precondition if credentials have been provided via external properties (to avoid placing valid credentials
     * inside the repository). If no credentials are provided, test will be skipped (and marked yellow in Jenkins).
     * Required properties are:
     * <ul>
     *   <li>test_user</li>
     *   <li>test_password</li>
     * </ul>
     * 
     * @param variable The variable containing the properties value
     * @param propertyName the name of the properties (one from above)
     * @param typeName The name of the properties (human readable name)
     */
    private static void assumeSpecified(String variable, String propertyName, String typeName) {
        Assumptions.assumeTrue(variable != null && !variable.isEmpty(), "No " + typeName + " specified, please specify "
            + "a test " + typeName + " via the property \"" + propertyName + "\", either on the command line via "
            + "-D" + propertyName + "= or on in maven/Eclipse via specifying system properties.");        
    }
    
    /**
     * Extracts user name and password from the VM args and aborts the test if they are not provided.
     * @return <tt>[userName, password]</tt>
     */
    public static String[] retreiveCredentialsFormVmArgs() {
        String userName = System.getProperty("test_user");
        String pw = System.getProperty("test_password");
        assumeSpecified(userName, "test_user", "user name");
        assumeSpecified(pw, "test_password", "password");
        
        return new String[] {userName, pw};
    }
    
    /**
     * Extracts credentials provided via VM arguments and logs in the user.
     * Useful to test authorized API calls.
     * @return The access token to access authorized API calls of the student management system.
     * @see #retreiveCredentialsFormVmArgs()
     */
    public static String retreiveAccessToken() {
        String[] credentials = retreiveCredentialsFormVmArgs();
        LoginComponent loginComp = new LoginComponent(TEST_AUTH_SERVER, TEST_MANAGEMENT_SERVER);
        try {
            Assumptions.assumeTrue(loginComp.login(credentials[0], credentials[1]));
        } catch (UnknownCredentialsException e) {
            Assertions.fail("Could not login due to unknown credentials: " + e.getMessage());
        } catch (ServerNotFoundException e) {
            Assertions.fail("Could not login due to unknown server specified: " + e.getMessage());
        }
        
        return loginComp.getManagementToken();
    }
}
