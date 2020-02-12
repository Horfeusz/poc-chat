package be.chat;

import be.chat.dto.MessageDTO;
import be.chat.dto.MessageDTOFactory;
import be.chat.remote.RemoteBeanUtil;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class ChatBean implements ChatRemote {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private ChatDb db;

    @Override
    public void sendMessageDTO(MessageDTO messageDTO) {
        logger.info("Adding message to DB: " + messageDTO);
        db.addMessage(messageDTO);
    }

    @Override
    public List<MessageDTO> getDTOMessages() {
        return db.getMessages();
    }
}
