package net.ssehub.rightsmanagement.conf;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.gsonfire.GsonFireBuilder;
import io.swagger.client.JSON;
import net.ssehub.exercisesubmitter.protocol.backend.LoginComponent;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;
import net.ssehub.rightsmanagement.Service;
import net.ssehub.rightsmanagement.logic.DataPullService;

/**
 * Singleton that reads and stores the configuration of the whole application (except for logging).
 * @author El-Sharkawy
 *
 */
public class Settings {
    
    public static final Settings INSTANCE = new Settings();
    
    private static final String SETTINGS_FILE = "settings.json";
    private static final Logger LOGGER = LogManager.getLogger(Service.class);
    
    private Configuration config;
    private JSON jsonParser;
    private LoginComponent loginComponent;
    
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
            Path pathToSettings;
            File inputFile = new File(SETTINGS_FILE);
            if (inputFile.exists()) {
                // Load relative to JAR
                LOGGER.debug("Loading application settings from {}.", inputFile.getAbsoluteFile());
                pathToSettings = inputFile.toPath();
            } else {
                // Load from resource folder while developing
                URL url = Settings.class.getResource("/" + SETTINGS_FILE);
                LOGGER.debug("Loading application settings from {}.", url);
                pathToSettings = Paths.get(url.toURI());
            }
            String content = Files.readString(pathToSettings);
            loadConfig(content);
        } catch (IOException e) {
            LOGGER.warn("Could not read configuration from {}, cause {}", SETTINGS_FILE, e);
            throw e;
        } catch (URISyntaxException e) {
            LOGGER.warn("Could not read configuration from {}, cause {}", SETTINGS_FILE, e);
            throw new IOException(e);
        }
        
        // Login into Management server if auth server is provided
        String authServer = getConfig().getAuthServerURL();
        String mgmtServer = getConfig().getMgmtServerURL();
        if (authServer != null) {
            LOGGER.debug("Provided authentication server, trying to log in via {}", authServer);
            
            LoginComponent login = new LoginComponent(authServer, mgmtServer);
            try {
                boolean success = login.login(getConfig().getAuthUser(), getConfig().getAuthPassword());
                if (success) {
                    loginComponent = login;
                    LOGGER.debug("Sucessfully logged in via {}", getConfig().getAuthServerURL());
                } else {
                    LOGGER.error("Could not reach one of the provided servers {} and {} to login into system for "
                        + "an unknown reason.", mgmtServer, authServer);
                }
            } catch (UnknownCredentialsException e) {
                String password = getConfig().getAuthPassword();
                boolean usesPassword = null != password && !password.isEmpty();
                
                LOGGER.error("Tried to login into {} via {} with user name {} and a password {}, but an error "
                    + "occured: {}", mgmtServer, authServer, getConfig().getAuthUser(), usesPassword, e.getMessage());
            } catch (ServerNotFoundException e) {
                LOGGER.error("Could not reach one of the provided servers {} and {} to login into system. Reason: {}",
                    mgmtServer, authServer, e.getMessage());
            }
        }
    }
    
    /**
     * Parsed the given configuration and loads it.
     * @param configAsJson The configuration to use at the whole application.
     */
    public void loadConfig(String configAsJson) {
        config = jsonParser.deserialize(configAsJson, Configuration.class);
    }
    
    /**
     * Saves the currently used configuration.
     * May be used to create a new configuration or to create test cases.
     * @param out The writer to save the configuration.
     */
    public void saveConfiguration(Writer out) {
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
    
    /**
     * Returns the login component. Maybe <tt>null</tt> if no auth server was specified or invalid credentials are
     * provided. Please check the logs in this case.
     * 
     * @return The login component, which provides the access tokens to be used for the {@link DataPullService}.
     */
    public LoginComponent getLogin() {
        return loginComponent;
    }
}
