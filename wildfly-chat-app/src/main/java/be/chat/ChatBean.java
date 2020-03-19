package be.chat;

import be.chat.dto.MessageDTO;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@PermitAll
public class ChatBean implements ChatRemote {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private ChatDb db;

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed({"manager"})
    @Override
    public void sendMessageDTO(MessageDTO messageDTO) {
        logger.info("Principal name: " + sessionContext.getCallerPrincipal().getName());
        db.addMessage(messageDTO);
    }

    @Override
    public List<MessageDTO> getDTOMessages() {
        return db.getMessages();
    }
}
