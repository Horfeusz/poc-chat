package be.chat;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface ChatRemote {

    void sendMessage(String message);

    List<String> getMessages();

}
