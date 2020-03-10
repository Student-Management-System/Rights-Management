package net.ssehub.rightsmanagement.rest.resources;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.client.JSON;
import io.swagger.client.model.UpdateMessage;

@Path("/hello")
public class UpdateCallback {

//    @GET
//    @Path("/{param}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public UserDto hello(@PathParam("param") String name) {
//        UserDto user = new UserDto();
//        user.setId(name);
//        return user;
//    }

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
        UpdateMessage msg = new JSON().deserialize(json, UpdateMessage.class);
        System.out.println("Got object: " + msg);
        return msg + "\n";
    }
}