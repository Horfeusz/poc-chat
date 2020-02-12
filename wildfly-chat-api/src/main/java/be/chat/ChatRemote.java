package be.chat;

import be.chat.dto.MessageDTO;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface ChatRemote {

    void sendMessageDTO(MessageDTO message);

    List<MessageDTO> getDTOMessages();

}
