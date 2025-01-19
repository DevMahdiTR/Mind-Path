package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.message.MessageDTO;
import mindpath.core.domain.message.UsersWithLastChat;
import mindpath.core.service.chat.ChatService;
import mindpath.core.utility.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIRouters.CHAT_ROUTER)
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        chatService.sendMessage(messageDTO);
        messagingTemplate.convertAndSend("/topic/%s/%s".formatted(messageDTO.sender(),messageDTO.receiver()), messageDTO);

        log.info("Sent message to user: {}", messageDTO.receiver());
    }



    @GetMapping("/messages/{receiverId}")
    public List<MessageDTO> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID receiverId) {
        return chatService.getMessages(userDetails,receiverId);
    }

    @GetMapping("/users")
    public CustomerResponse<List<UsersWithLastChat>> getChatUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return new CustomerResponse<>(chatService.getChatUsers(userDetails), HttpStatus.OK);
    }
}
