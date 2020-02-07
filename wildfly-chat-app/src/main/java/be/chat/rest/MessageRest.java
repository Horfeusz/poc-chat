package be.chat.rest;

import be.chat.ChatRemote;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.List;

@Path("/message")
public class MessageRest {

    @EJB
    private ChatRemote chat;

    @PUT
    public void sendMessage(String message) {
        chat.sendMessage(message);
    }

    @GET
    public List<String> getMessages() {
        return chat.getMessages();
    }

}
