package be.chat;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatDb {

    private List<String> messages;

    @PostConstruct
    private void init() {
        messages = new ArrayList<>();
    }

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
