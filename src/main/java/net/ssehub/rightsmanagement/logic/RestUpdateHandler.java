package net.ssehub.rightsmanagement.logic;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

/**
 * Pulls with each update message the whole configuration from the student management system.
 * @author El-Sharkawy
 *
 */
public class RestUpdateHandler extends AbstractUpdateHandler {
    
    /**
     * Creates an {@link AbstractUpdateHandler} that pulls always the complete information from the student management
     * system at each update request.
     * @param courseConfig The configuration for the managed course.
     */
    public RestUpdateHandler(CourseConfiguration courseConfig) {
        super(courseConfig);
    }
    
    /**
     * Internal constructor for testing purpose.
     * @param courseConfig The configuration for the managed course.
     * @param connector The connector to use {@link DataPullService#DataPullService(CourseConfiguration)} or
     *      <tt>null</tt> during tests.
     */
    protected RestUpdateHandler(CourseConfiguration courseConfig, DataPullService connector) {
        super(courseConfig, connector);
    }

    @Override
    protected Course computeFullConfiguration(NotificationDto msg) {
        return getDataPullService().computeFullConfiguration();
    }

}
