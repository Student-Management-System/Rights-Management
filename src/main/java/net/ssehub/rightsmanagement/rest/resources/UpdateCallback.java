package net.ssehub.rightsmanagement.rest.resources;
import java.io.IOException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import io.swagger.client.JSON;
import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.logic.UpdateChangeListener;
import net.ssehub.rightsmanagement.logic.WrongFormatException;

/**
 * Listens at <tt>server/rest/update/</tt> for changes at the student management system.
 * @author El-Sharkawy
 *
 */
@Path("/update")
public class UpdateCallback {

    private static final Logger LOGGER = LogManager.getLogger(UpdateCallback.class);
    
    /**
     * Retrieves a JSON message which was specified as <tt>text/plain</tt> converts it and handles the message.
     * @param update A {@link UpdateMessage} in serialized as JSON.
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void helloUsingTxt(String update) {
        LOGGER.debug("Received plain text message = {}", update);
        processMessage(update);
    }
    
    /**
     * Retrieves a JSON message which was specified as <tt>application/json</tt> converts it and handles the message.
     * @param json A {@link UpdateMessage} in serialized as JSON.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void helloUsingJson(String json) {
        LOGGER.debug("Received JSON message = {}", json);
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
            // Malformed input -> client side error
            throw new BadRequestException(e);
        }
        
        try {
            LOGGER.debug("Processing update message = {}", json);
            UpdateChangeListener.INSTANCE.onChange(msg);
        } catch (WrongFormatException e) {
            // Malformed input -> client side error
            throw new NotAcceptableException(e);
        } catch (IOException e) {
            // internal error -> server side error
            throw new InternalServerErrorException(e);
        }
    }
}