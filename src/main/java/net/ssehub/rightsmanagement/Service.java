package net.ssehub.rightsmanagement;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.logic.AbstractUpdateHandler;
import net.ssehub.rightsmanagement.logic.UpdateChangeListener;
import net.ssehub.rightsmanagement.logic.UpdateChangeListener.UpdateStrategy;
import net.ssehub.rightsmanagement.rest.RestServer;

/**
 * Main class that listens for changes and applies these changes to the observed SVN repository.
 * @author El-Sharkawy
 *
 */
public class Service {
    private static final Logger LOGGER = LogManager.getLogger(Service.class);

    /**
     * Starting point of this service.
     * @param args Will be ignored
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {        
        try {
            Settings.INSTANCE.init();
        } catch (IOException e) {
            // Abort application
            LOGGER.fatal("Could not load configuration", e);
            System.exit(1);
        }
        
        List<CourseConfiguration> courses = Settings.getConfig().getCourses();
        for (CourseConfiguration courseConfiguration : courses) {
            UpdateStrategy strategy = courseConfiguration.getUpdateStrategy();
            if (null != strategy) {
                strategy = UpdateStrategy.IMMEDIATELY;
            }
            AbstractUpdateHandler handler = UpdateChangeListener.INSTANCE.createHandler(courseConfiguration, strategy);
            UpdateChangeListener.INSTANCE.register(handler); 
        }
        
        new RestServer(Settings.getConfig().getRestPort());
    }

}
