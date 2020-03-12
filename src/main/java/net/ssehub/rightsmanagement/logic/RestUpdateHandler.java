package net.ssehub.rightsmanagement.logic;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.model.Course;

/**
 * Pulls with each update message the whole configuration from the student management system.
 * @author El-Sharkawy
 *
 */
public class RestUpdateHandler extends AbstractUpdateHandler {
    
    private static final Logger LOGGER = Log.getLog();

    private DataPullService connector;
    
    /**
     * Creates an {@link AbstractUpdateHandler} that pulls always the complete information from the student management
     * system at each update request.
     * @param courseConfig The configuration for the managed course.
     */
    public RestUpdateHandler(CourseConfiguration courseConfig) {
        super(courseConfig);
        connector = new DataPullService(courseConfig);
    }

    @Override
    protected Course computeFullConfiguration(UpdateMessage msg) {
        return connector.computeFullConfiguration();
    }

}
