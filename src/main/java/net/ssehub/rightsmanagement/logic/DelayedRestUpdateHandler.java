package net.ssehub.rightsmanagement.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

/**
 * A {@link RestUpdateHandler} that does not pull immediately new data. Instead it waits for a certain time for
 * further changes to reduce the overall workload / traffic.
 * @author El-Sharkawy
 *
 */
public class DelayedRestUpdateHandler extends RestUpdateHandler {
    
    private static final Logger LOGGER = LogManager.getLogger(DelayedRestUpdateHandler.class);

    /**
     * Wait 10 seconds before checking if service should pull the whole configuration from the student management system
     * to apply the update.
     */
    private static final long SLEEP_TIME = 10 * 1000;
    
    private final long delay;
    private long lastUpdate;
    
    /**
     * Creates a new {@link RestUpdateHandler} that waits for the specified time for further changes.
     * @param courseConfig The configuration for the managed course.
     * @param delay the time to wait for further changes in milli seconds.
     */
    public DelayedRestUpdateHandler(CourseConfiguration courseConfig, long delay) {
        super(courseConfig);
        this.delay = delay;
        this.lastUpdate = 0;
    }
    
    @Override
    protected Course computeFullConfiguration(NotificationDto msg) throws NetworkException {
        while (System.currentTimeMillis() - lastUpdate < delay) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.warn("Could not sleep thread of " + getClass().getSimpleName() , e);
            }
        }
        
        lastUpdate = System.currentTimeMillis();
        return super.computeFullConfiguration(msg);
    }

}
