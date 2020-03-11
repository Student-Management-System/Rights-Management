package net.ssehub.rightsmanagement.rest.resources;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.google.gson.JsonParseException;

import io.swagger.client.JSON;
import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.update.UpdateChangeListener;
import net.ssehub.rightsmanagement.update.WrongFormatException;

/**
 * Listens at <tt>server/rest/update/</tt> for changes at the student management system.
 * @author El-Sharkawy
 *
 */
@Path("/update")
public class UpdateCallback {

    private static final Logger LOGGER = Log.getLog();
    
    /**
     * Retrieves a JSON message which was specified as <tt>text/plain</tt> converts it and handles the message.
     * @param update A {@link UpdateMessage} in serialized as JSON.
     * @return
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void helloUsingTxt(String update) {
        LOGGER.debug("Received plain text message", update);
        processMessage(update);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void helloUsingJson(String json) {
        LOGGER.debug("Received JSON message", json);
        processMessage(json);
    }

    /**
     * Parsed the JSON message and processes the the update via {@link UpdateChangeListener}.
     * @param json A JSON representation of {@link UpdateMessage}.
     */
    private void processMessage(String json) {
        UpdateMessage msg;
        try {
            msg = new JSON().deserialize(json, UpdateMessage.class);
        } catch (JsonParseException e) {
            LOGGER.warn("Could not parse message {} to {}, cause {}", json, UpdateMessage.class.getSimpleName(), e);
            throw new BadRequestException(e);
        }
        
        try {
            UpdateChangeListener.INSTANCE.onChange(msg);
        } catch (WrongFormatException e) {
            throw new NotAcceptableException(e);
        }
    }
}