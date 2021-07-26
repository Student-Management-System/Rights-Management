package net.ssehub.rightsmanagement.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple heartbeat route to check if the server is up.
 * 
 * @author Adam
 */
@Path("/heartbeat")
public class Heartbeat {
    
    private static final Logger LOGGER = LogManager.getLogger(Heartbeat.class);

    /**
     * A simple heartbeat response that returns 200 if the server is up.
     */
    @GET
    public void heartbeat() {
        LOGGER.debug("Heartbeat request received");
    }
    
}
