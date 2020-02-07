package be.chat.rest;

import java.util.ArrayList;
import java.util.List;

public class MessagesResponse {

    private List<String> messages;

    public MessagesResponse() {
        this.messages = new ArrayList<>();
    }

    public MessagesResponse(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

}
