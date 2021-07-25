package net.ssehub.rightsmanagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.logic.AbstractUpdateHandler;
import net.ssehub.rightsmanagement.logic.UpdateChangeListener;
import net.ssehub.rightsmanagement.logic.UpdateChangeListener.UpdateStrategy;
import net.ssehub.rightsmanagement.rest.RestServer;
import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.api.NotificationApi;
import net.ssehub.studentmgmt.backend_api.model.SubscriberDto;
import net.ssehub.studentmgmt.sparkyservice_api.ApiException;
import net.ssehub.studentmgmt.sparkyservice_api.api.AuthControllerApi;
import net.ssehub.studentmgmt.sparkyservice_api.model.AuthenticationInfoDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.CredentialsDto;

/**
 * Main class that listens for changes and applies these changes to the observed SVN repository.
 * @author El-Sharkawy
 *
 */
public class Service {
    private static final Logger LOGGER = LogManager.getLogger(Service.class);

    private String listenerId = String.format("svn-rights-management-%04d",
            (int) (Math.random() * 10000));
    
    private List<String> subscribedCourses = new LinkedList<>();
    
    /**
     * Creates an authenticated API client.
     * 
     * @return The API client, or <code>null</code> if authentication failed.
     */
    private ApiClient getAuthenticatedMgmtClient() {
        net.ssehub.studentmgmt.sparkyservice_api.ApiClient authClient
                = new net.ssehub.studentmgmt.sparkyservice_api.ApiClient();
        authClient.setBasePath(Settings.getConfig().getAuthServerURL());
        
        AuthControllerApi authApi = new AuthControllerApi(authClient);
        
        CredentialsDto credentials = new CredentialsDto();
        credentials.setUsername(Settings.getConfig().getAuthUser());
        credentials.setPassword(Settings.getConfig().getAuthPassword());
        
        ApiClient mgmtClient = null;
        try {
            AuthenticationInfoDto authResult = authApi.authenticate(credentials);
            
            mgmtClient = new ApiClient();
            mgmtClient.setBasePath(Settings.getConfig().getMgmtServerURL());
            mgmtClient.setAccessToken(authResult.getToken().getToken());
        } catch (ApiException e) {
            LOGGER.error("Failed to authenticate: " + e.getCode() + ", " + e.getResponseBody());
        }
        
        return mgmtClient;
    }
    
    /**
     * Registers this service as a listener in the management system.
     * 
     * @return If registering succeeded.
     */
    private synchronized boolean registerListener() {
        ApiClient mgmtClient = getAuthenticatedMgmtClient();
        if (mgmtClient != null) {
            NotificationApi notificationApi = new NotificationApi(mgmtClient);
            
            SubscriberDto subscriber = new SubscriberDto();
            subscriber.setName(listenerId);
            subscriber.setUrl(Settings.getConfig().getRestPath());
            
            Map<String, Boolean> events = new HashMap<>();
            events.put("ALL", true);
            subscriber.setEvents(events);
            
            LOGGER.info("Subscribing as listener " + listenerId + " with URL " + subscriber.getUrl()
                + " to all events");
            
            for (CourseConfiguration courseConfig : Settings.getConfig().getCourses()) {
                String courseId = courseConfig.getCourseName() + "-" + courseConfig.getSemester();
                try {
                    notificationApi.subscribe(subscriber, courseConfig.getCourseName() + "-"
                            + courseConfig.getSemester(), listenerId);
                    
                    LOGGER.info("Subscribed to course " + courseId);
                    
                    subscribedCourses.add(courseId);
                } catch (net.ssehub.studentmgmt.backend_api.ApiException e) {
                    LOGGER.error("Failed to subscribe to course " + courseId + ": " + e.getCode()
                            + ", " + e.getResponseBody());
                }
            }
            
        }
        return subscribedCourses.size() != 0;
    }
    
    /**
     * Unregisters this service as a listener from the management system.
     */
    private synchronized void unregisterListener() {
        ApiClient mgmtClient = getAuthenticatedMgmtClient();
        if (mgmtClient == null) {
            return;
        }
        NotificationApi notificationApi = new NotificationApi(mgmtClient);
        
        for (String courseId : subscribedCourses) {
            try {
                notificationApi.unsubscribe(courseId, listenerId);
                LOGGER.info("Unsubscribed from course " + courseId);
                
            } catch (net.ssehub.studentmgmt.backend_api.ApiException e) {
                LOGGER.error("Failed to unsubscribe from course " + courseId + ": " + e.getCode()
                    + ", " + e.getResponseBody());
            }
        }
        subscribedCourses.clear();
    }
    
    /**
     * Runs this service.
     */
    // checkstyle: stop exception type check (used interface of library throws exception)
    public void run() throws Exception {
    // checkstyle: resume exception type check
        try {
            Settings.INSTANCE.checkForInitializationError();
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
        
        try (RestServer server = new RestServer(Settings.getConfig().getRestPort())) {
            while (!registerListener()) {
                LOGGER.error("Failed to register this service as a listener in the management instance"
                        + ", retrying in 10 seconds...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
            }
            
            boolean success = false;
            while (!success) {
                try {
                    server.join();
                    success = true;
                } catch (InterruptedException e) {
                }
            }
        } finally {
            unregisterListener();
        }
        
    }
    
    /**
     * Starting point of this service.
     * @param args Will be ignored
     */
    // checkstyle: stop exception type check (used interface of library throws exception)
    public static void main(String[] args) throws Exception {
    // checkstyle: resume exception type check
        new Service().run();
    }

}
