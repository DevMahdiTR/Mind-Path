package mindpath.core.service.chat;


import mindpath.core.domain.message.MessageDTO;
import mindpath.core.domain.message.UsersWithLastChat;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    MessageDTO sendMessage(final MessageDTO messageDTO);
    List<MessageDTO> getMessages(final UserDetails userDetails ,final UUID receiverId);
    List<UsersWithLastChat> getChatUsers(final UserDetails userDetails);
    MessageDTO findLastMessageBySenderAndReceiver(final String senderEmail, final String receiverEmail);
}
