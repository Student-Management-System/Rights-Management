package net.ssehub.rightsmanagement.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.AccessWriter;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.svn.Repository;
import net.ssehub.rightsmanagement.svn.RepositoryNotFoundException;

/**
 * Handles the updates for <b>one</b> repository.
 * @author El-Sharkawy
 *
 */
public abstract class AbstractUpdateHandler {
    
    private static final Logger LOGGER = LogManager.getLogger(AbstractUpdateHandler.class);
    
    private CourseConfiguration courseConfig;
    private DataPullService connector;
    
    /**
     * Creates a handler to manage updates for a course.
     * @param courseConfig The configuration for the managed course.
     */
    public AbstractUpdateHandler(CourseConfiguration courseConfig) {
        this(courseConfig, new DataPullService(courseConfig));
    }
    
    /**
     * Alternative constructor for testing purpose.
     * @param courseConfig The configuration for the managed course.
     * @param connector A manually created {@link DataPullService}
     *     (otherwise the configuration will be used to create it).
     */
    protected AbstractUpdateHandler(CourseConfiguration courseConfig, DataPullService connector) {
        this.courseConfig = courseConfig;
        this.connector = connector;
    }
    
    /**
     * Returns the connector to pull information from the <b>student management system</b> via REST.
     * @return The connection to the <b>student management system</b>.
     */
    protected DataPullService getDataPullService() {
        return connector;
    }
    
    /**
     * Handles the update request and updates the managed repository.
     * @param msg The update request produced by the student management service
     * @throws IOException If the local repository couldn't be updated.
     */
    public synchronized void update(UpdateMessage msg) throws IOException {
        LOGGER.debug("Received update message \"{}\" for course \"{}\" processed by \"{}\"", msg, getCourseID(),
            getClass().getSimpleName());
        
        /*
         * First: Compute whole set-up for the course out of the delta.
         * This is required, since the access file can only be written for the complete course at once.
         */
        Course course = computeFullConfiguration(msg);
        
        /*
         * Second: Write access file.
         * Should be done before updating the repository to avoid accidentally access to newly files and folders.
         */
        AccessWriter writer = createWriter();
        try {
            writer.write(course, courseConfig.getSvnName());
        } finally {
            writer.close();
        }
        
        
        /*
         * Third: Update repository.
         */
        updateRepository(course);
    }
    
    /**
     * Returns the configuration of the managed course, which is updated by this handler.
     * @return The configuration of the managed course.
     */
    protected final CourseConfiguration getConfig() {
        return courseConfig;
    }
    
    /**
     * Estimates the full course configuration out of the update message.
     * May be overwritten in child classes to realize different update strategies.
     * @param msg The update request produced by the student management service
     * @return The complete set-up for the whole course.
     */
    protected abstract Course computeFullConfiguration(UpdateMessage msg);
    
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
     * @throws IOException
     */
    protected void updateRepository(Course course) throws IOException {
        try {
            Repository repository = new Repository(courseConfig.getRepositoryPath());
            for (Assignment assignment : course.getAssignments()) {
                repository.createOrModifyAssignment(assignment);
            }
        } catch (SVNException | RepositoryNotFoundException e) {
            throw new IOException(e);
        }
    }

    /**
     * Returns the ID of the managed course.
     * @return The ID of the course, won't be <tt>null</tt>.
     */
    public String getCourseID() {
        return courseConfig.getCourseName() + "-" + courseConfig.getSemester();
    }
}
