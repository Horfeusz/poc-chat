package be.chat;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class ChatBean implements ChatRemote {

    @Inject
    private ChatDb db;

    @Override
    public void sendMessage(String message) {
        db.addMessage(message);
    }

    @Override
    public List<String> getMessages() {
        return db.getMessages();
    }
}
