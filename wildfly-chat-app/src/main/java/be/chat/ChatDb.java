package be.chat;

import be.chat.dto.MessageDTO;
import com.google.common.collect.Lists;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatDb {

    @Getter
    private List<MessageDTO> messages;

    @PostConstruct
    private void init() {
        messages = Lists.newArrayList();
    }
    
    public void addMessage(MessageDTO message) {
        messages.add(message);
    }

}

