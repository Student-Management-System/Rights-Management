package net.ssehub.rightsmanagement.logic;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.model.Course;

/**
 * Pulls with each update message the whole configuration from the student management system.
 * @author El-Sharkawy
 *
 */
public class RestUpdateHandler extends AbstractUpdateHandler {
    
    private DataPullService connector;
    
    /**
     * Creates an {@link AbstractUpdateHandler} that pulls always the complete information from the student management
     * system at each update request.
     * @param courseConfig The configuration for the managed course.
     */
    public RestUpdateHandler(CourseConfiguration courseConfig) {
        this(courseConfig, new DataPullService(courseConfig));
    }
    
    /**
     * Internal constructor for testing purpose.
     * @param courseConfig The configuration for the managed course.
     * @param connector The connector to use {@link DataPullService#DataPullService(CourseConfiguration)} or
     *      <tt>null</tt> during tests.
     */
    protected RestUpdateHandler(CourseConfiguration courseConfig, DataPullService connector) {
        super(courseConfig);
        this.connector = connector;
    }

    @Override
    protected Course computeFullConfiguration(UpdateMessage msg) {
        return connector.computeFullConfiguration();
    }

}
