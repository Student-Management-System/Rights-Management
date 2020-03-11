package net.ssehub.rightsmanagement.conf;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.google.gson.Gson;

import io.gsonfire.GsonFireBuilder;
import io.swagger.client.JSON;

/**
 * Singleton that reads and stores the configuration of the whole application (except for logging).
 * @author El-Sharkawy
 *
 */
public class Settings {
    
    public static final Settings INSTANCE = new Settings();
    
    private static final String settingsFile = "settings.json";
    private static final Logger LOGGER = Log.getLog();
    
    private Configuration config;
    private JSON jsonParser;
    
    /**
     * Singleton constructor.
     */
    private Settings() {
        jsonParser = new JSON();
        Gson gson = new GsonFireBuilder().createGsonBuilder()
            .setPrettyPrinting()
            .create();
        jsonParser.setGson(gson);
      
    }
    
    /**
     * Loads the configuration from the settings file.
     * Needs to be done <b>once</b> at startup.
     * @throws IOException If the default configuration could not be read.
     */
    public void init() throws IOException {
        // Based on https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
        try {
            URL url = Settings.class.getResource(settingsFile);
            String content = Files.readString(Paths.get(url.toURI()));
            loadConfig(content);
        } catch (IOException e) {
            LOGGER.warn("Could not read configuration from {}, cause {}", settingsFile, e);
            throw e;
        } catch (URISyntaxException e) {
            LOGGER.warn("Could not read configuration from {}, cause {}", settingsFile, e);
            throw new IOException(e);
        }
    }
    
    void loadConfig(String configAsJson) {
        config = jsonParser.deserialize(configAsJson, Configuration.class);
    }
    
    void saveConfiguration(Writer out) {
        String configAsJson = jsonParser.serialize(config);
        try {
            out.write(configAsJson);
            out.flush();
        } catch (IOException e) {
            LOGGER.warn("Could not save configuration, cause {}", e);
        }
    }
    
    /**
     * Returns the whole configuration.
     * Static method only for convince reasons. 
     * @return The configuration of the application.
     */
    public static Configuration getConfig() {
        return INSTANCE.config;
    }
}
