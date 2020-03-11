package net.ssehub.rightsmanagement;

import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.rest.RestServer;

/**
 * Main class that listens for changes and applies these changes to the observed SVN repository.
 * @author El-Sharkawy
 *
 */
public class Service {

    public static void main(String[] args) {
        new RestServer(Settings.INSTANCE.getConfig().getRestPort());
    }

}
