package be.chat;

import be.chat.dto.MessageDTO;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
@PermitAll
public class ChatDb {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Getter
    private List<MessageDTO> messages;

    @PostConstruct
    private void init() {
        messages = Lists.newArrayList();
    }

    @RolesAllowed({"manager"})
    public void addMessage(MessageDTO message) {
        Optional.ofNullable(message)
                .ifPresent(m -> {
                    messages.add(m);
                    logger.info("Added a message to DB: " + m);
                });
    }

}

