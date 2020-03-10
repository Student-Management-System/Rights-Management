package net.ssehub.rightsmanagement.rest.resources;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonParseException;

import io.swagger.client.JSON;
import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.StudentManagementChangeListener;

/**
 * Listens at <tt>server/rest/update/</tt> for changes at the student management system.
 * @author El-Sharkawy
 *
 */
@Path("/update")
public class UpdateCallback {

    /**
     * Retrieves a JSON message which was specified as <tt>text/plain</tt> converts it and handles the message.
     * @param update A {@link UpdateMessage} in serialized as JSON.
     * @return
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String helloUsingTxt(String update) {
        System.out.println("Got String: " + update);
        return helloUsingJson(update);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String helloUsingJson(String json) {
        UpdateMessage msg;
        try {
            msg = new JSON().deserialize(json, UpdateMessage.class);
        } catch (JsonParseException e) {
            throw new BadRequestException(e);
        }
        
        boolean allOK = StudentManagementChangeListener.INSTANCE.onChange(msg);
        if (!allOK) {
            throw new NotAcceptableException("Course not managed by this service.");
        }
        return "\n";
    }
}