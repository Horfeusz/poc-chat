package be.chat.rest;

import be.chat.ChatRemote;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MessageRest {

    @EJB
    private ChatRemote chat;

    @PUT
    public Response sendMessage(String message) {
        chat.sendMessage(message);
        return Response.ok().build();
    }

    @GET
    public Response getMessages() {
        return Response.ok(new MessagesResponse(chat.getMessages()), MediaType.APPLICATION_JSON_TYPE).build();
    }

}
