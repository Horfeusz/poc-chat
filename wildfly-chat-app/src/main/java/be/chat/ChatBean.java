package be.chat;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ChatBean implements ChatRemote {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private ChatDb db;

    @Override
    public void sendMessage(String message) {
        logger.info("Adding message: " + message);
        db.addMessage(message);
    }

    @Override
    public List<String> getMessages() {
        logger.info("Getting messages");
        return db.getMessages();
    }
}
