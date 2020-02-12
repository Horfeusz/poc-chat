package be.chat.rest;

import be.chat.ChatRemote;
import be.chat.dto.MessageDTO;
import be.chat.dto.MessageDTOFactory;
import be.chat.remote.RemoteBeanUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MessageRest {

    @EJB
    private MessageDTOFactory messageDTOFactory;

    @EJB
    private ChatRemote chat;

    @PUT
    public Response sendMessage(MessagesContainer messagesContainer) {
        Optional.of(messagesContainer)
                .map(MessagesContainer::getMessages)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(messages -> messages.stream()
                        .map(messageDTOFactory::create)
                        .forEach(messageDTO -> {
                            chat.sendMessageDTO(messageDTO);

                            //I try send messages to GlassFish
                            RemoteBeanUtil.lookup(ChatRemote.class)
                                    .ifPresent(chatRemote ->
                                            chatRemote.sendMessageDTO(messageDTO));
                        }));

        return Response.ok().build();
    }

    @GET
    public List<MessageDTO> getDtoMessages() {
        return chat.getDTOMessages();
    }

}
