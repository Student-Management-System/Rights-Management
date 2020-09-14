package net.ssehub.rightsmanagement.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.RightsManagementProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.User;
import net.ssehub.rightsmanagement.AccessWriter;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.svn.Repository;
import net.ssehub.rightsmanagement.svn.RepositoryNotFoundException;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

/**
 * Handles the updates for <b>one</b> repository.
 * @author El-Sharkawy
 *
 */
public abstract class AbstractUpdateHandler {
    
    private static final Logger LOGGER = LogManager.getLogger(AbstractUpdateHandler.class);
    
    private CourseConfiguration courseConfig;
    private RightsManagementProtocol connector;
    
    /**
     * Creates a handler to manage updates for a course.
     * @param courseConfig The configuration for the managed course.
     */
    public AbstractUpdateHandler(CourseConfiguration courseConfig) {
        this(courseConfig, createProtocol(courseConfig));
        
        if (courseConfig.isInitRepositoryIfNotExists()) {
            try {
                LOGGER.debug("Initialize repository if it does not exist at {}.", courseConfig.getRepositoryPath());
                new Repository(courseConfig.getRepositoryPath(), courseConfig.getAuthor(), true);
                LOGGER.debug("Initialized repository if it does not existed at {}.", courseConfig.getRepositoryPath());
            } catch (RepositoryNotFoundException e) {
                LOGGER.fatal("Could not initlaize repository at " + courseConfig.getRepositoryPath(), e);
            }
        }
    }
    
    /**
     * Alternative constructor for testing purpose.
     * @param courseConfig The configuration for the managed course.
     * @param connector A manually created {@link RightsManagementProtocol}
     *     (otherwise the configuration will be used to create it).
     */
    protected AbstractUpdateHandler(CourseConfiguration courseConfig, RightsManagementProtocol connector) {
        this.courseConfig = courseConfig;
        this.connector = connector;
    }
    
    /**
     * Part of the constructor to create a {@link RightsManagementProtocol} to use.
     * @param courseConfig The configuration for the managed course. 
     * @return The network protocol to use to query the student management service.
     */
    private static RightsManagementProtocol createProtocol(CourseConfiguration courseConfig) {
        RightsManagementProtocol protocol = new RightsManagementProtocol(Settings.getConfig().getAuthServerURL(),
            Settings.getConfig().getMgmtServerURL(), courseConfig.getCourseName(), courseConfig.getSemester());
        
        String authServer = Settings.getConfig().getAuthServerURL();
        String mgmtServer = Settings.getConfig().getMgmtServerURL();
        if (authServer != null) {
            LOGGER.debug("Provided authentication server, trying to log in via {}", authServer);
            
            String user = Settings.getConfig().getAuthUser();
            String password = Settings.getConfig().getAuthPassword();
            try {
                boolean success = protocol.login(user, password);
                if (success) {
                    LOGGER.debug("Sucessfully logged in via {}", authServer);
                } else {
                    LOGGER.error("Could not reach one of the provided servers {} and {} to login into system for "
                        + "an unknown reason.", mgmtServer, authServer);
                }
            } catch (UnknownCredentialsException e) {
                boolean usesPassword = null != password && !password.isEmpty();
                
                LOGGER.error("Tried to login into {} via {} with user name {} and a password {}, but an error "
                    + "occured: {}", mgmtServer, authServer, user, usesPassword, e.getMessage());
            } catch (ServerNotFoundException e) {
                LOGGER.error("Could not reach one of the provided servers {} and {} to login into system. Reason: {}",
                    mgmtServer, authServer, e.getMessage());
            }
        }
        
        return protocol;
    }
    
    /**
     * Returns the connector to pull information from the <b>student management system</b> via REST.
     * @return The connection to the <b>student management system</b>.
     */
    protected RightsManagementProtocol getDataPullService() {
        return connector;
    }
    
    /**
     * Handles the update request and updates the managed repository.
     * @param msg The update request produced by the student management service
     * @throws IOException If the local repository couldn't be updated.
     */
    public synchronized void update(NotificationDto msg) throws IOException {
        LOGGER.debug("Received update message \"{}\" for course \"{}\" processed by \"{}\"", msg, getCourseID(),
            getClass().getSimpleName());
        
        try {
            /*
             * First: Compute whole set-up for the course out of the delta.
             * This is required, since the access file can only be written for the complete course at once.
             */
            Course course = computeFullConfiguration(msg);
            
            /*
             * Second: Update repository.
             */
            Set<String> deprecatedFolders = updateRepository(course);
            
            /*
             * Third: Write access file.
             */
            AccessWriter writer = createWriter();
            try {
                writer.write(course, courseConfig.getSvnName(), deprecatedFolders);
            } catch (IOException e) {
                // Use logging with concatenation here:
                // As is is almost always printed when reached and to print stack trace
                LOGGER.error("Could not write access file for course \"" + getCourseID() + "\".", e);
            } finally {
                writer.close();
            }
        } catch (NetworkException netExc) {
            LOGGER.error("Could not query required information, cause: " + netExc.getMessage(), netExc);
        }
        
    }
    
    /**
     * Returns the configuration of the managed course, which is updated by this handler.
     * @return The configuration of the managed course.
     */
    protected final CourseConfiguration getConfig() {
        return courseConfig;
    }
    
    /**
     * (re-)loads the complete information from the <b>student management server</b>, which is required to configure
     * the submission repository.
     * Doesn't use any cached information.
     * @return The information to configure the submission repository
     * @throws NetworkException If network problems occur
     */
    protected Course computeFullConfiguration() throws NetworkException {
        Course course = new Course();
        course.setCourseName(courseConfig.getCourseName());
        course.setSemester(courseConfig.getSemester());
        
        // update tutors
        Group tutors = getDataPullService().getTutors();
        course.setTutors(tutors);
        
        // update list of all participants
        List<User> studentsOfCourse = getDataPullService().getStudents();
        course.setStudents(studentsOfCourse);
        
        // update all non-group assignments, as the list of students has changed
        List<ManagedAssignment> assignments = getDataPullService().loadAssignments(studentsOfCourse);
        course.setAssignments(assignments);
        
        return course;
    }
    
    /**
     * Estimates the full course configuration out of the update message.
     * May be overwritten in child classes to realize different update strategies.
     * @param msg The update request produced by the student management service
     * @return The complete set-up for the whole course.
     */
    protected abstract Course computeFullConfiguration(NotificationDto msg) throws NetworkException;
    
    /**
     * Creates the {@link AccessWriter} to write the access file.
     * May be overwritten for testing purposes.
     * @return The writer to use.
     * @throws IOException If the named file exists but is a directory rather than a regular file,
     *     does not exist but cannot be created, or cannot be opened for any other reason
     */
    protected AccessWriter createWriter() throws IOException {
        Writer writer = new FileWriter(courseConfig.getAccessPath());
        return new AccessWriter(writer);
    }
    
    /**
     * Updates the repository.
     * May be overwritten for testing purposes or to support alternative repositories.
     * @param course The data to be reflected in access file and SVN repository.
     * @return A list of assignment and submission folders which exist but are no longer used and thus should be
     *     hidden via the access file. May be <tt>null</tt> in case of errors, or empty if there are no problematic
     *     folders to black list.
     * @throws IOException If the repository cannot be read.
     */
    protected Set<String> updateRepository(Course course) throws IOException {
        Set<String> folders = null;
        try {
            Repository repository = new Repository(courseConfig.getRepositoryPath(), courseConfig.getAuthor(), false);
            folders = repository.updateRepository(course);
        } catch (SVNException | RepositoryNotFoundException e) {
            throw new IOException(e);
        }
        
        return folders;
    }

    /**
     * Returns the ID of the managed course.
     * @return The ID of the course, won't be <tt>null</tt>.
     */
    public String getCourseID() {
        return courseConfig.getCourseName() + "-" + courseConfig.getSemester();
    }
}
