package mindpath.core.mapper;

import mindpath.core.domain.message.Message;
import mindpath.core.domain.message.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MessageDTOMapper implements Function<Message, MessageDTO> {
    @Override
    public MessageDTO apply(Message message) {
        return new MessageDTO(
                message.getId() == null ? null : message.getId(),
                message.getContent(),
                message.getSender() == null ? null : message.getSender().getUsername(),
                message.getReceiver() == null ? null : message.getReceiver().getUsername(),
                message.getTimestamp()
        );
    }
}
