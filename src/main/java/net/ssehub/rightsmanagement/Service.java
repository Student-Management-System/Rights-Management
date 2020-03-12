package net.ssehub.rightsmanagement;

import java.io.IOException;
import java.util.List;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.logic.AbstractUpdateHandler;
import net.ssehub.rightsmanagement.logic.IncrementalUpdateHandler;
import net.ssehub.rightsmanagement.logic.RestUpdateHandler;
import net.ssehub.rightsmanagement.logic.UpdateChangeListener;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.rest.RestServer;

/**
 * Main class that listens for changes and applies these changes to the observed SVN repository.
 * @author El-Sharkawy
 *
 */
public class Service {

    private static final boolean PULL_FULL_CONFIG_ON_UPDATE = true;
    
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
            
            System.exit(1);
        }
        
        List<CourseConfiguration> courses = Settings.getConfig().getCourses();
        for (CourseConfiguration courseConfiguration : courses) {
            AbstractUpdateHandler handler =
                UpdateChangeListener.INSTANCE.createHandler(courseConfiguration, PULL_FULL_CONFIG_ON_UPDATE);
            UpdateChangeListener.INSTANCE.register(handler); 
        }
        
        new RestServer(Settings.getConfig().getRestPort());
    }

}
