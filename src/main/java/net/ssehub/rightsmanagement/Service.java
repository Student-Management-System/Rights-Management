package net.ssehub.rightsmanagement;

import java.io.IOException;
import java.util.List;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.rest.RestServer;
import net.ssehub.rightsmanagement.rest.update.IncrementalUpdateHandler;
import net.ssehub.rightsmanagement.rest.update.UpdateChangeListener;

/**
 * Main class that listens for changes and applies these changes to the observed SVN repository.
 * @author El-Sharkawy
 *
 */
public class Service {

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
            UpdateChangeListener.INSTANCE.register(new IncrementalUpdateHandler(courseConfiguration)); 
        }
        
        new RestServer(Settings.getConfig().getRestPort());
    }

}
