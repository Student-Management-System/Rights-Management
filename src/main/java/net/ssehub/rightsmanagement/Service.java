package net.ssehub.rightsmanagement;

import java.io.IOException;

import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.rest.RestServer;

/**
 * Main class that listens for changes and applies these changes to the observed SVN repository.
 * @author El-Sharkawy
 *
 */
public class Service {

    public static void main(String[] args) {
        try {
            Settings.INSTANCE.init();
        } catch (IOException e) {
            // Abort application
            System.exit(1);
        }
        
        new RestServer(Settings.getConfig().getRestPort());
    }

}
