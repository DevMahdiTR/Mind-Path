package mindpath.core.service.chat;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.group.Group;
import mindpath.core.domain.message.Message;
import mindpath.core.domain.message.MessageDTO;
import mindpath.core.domain.message.UsersWithLastChat;
import mindpath.core.domain.subject.Subject;
import mindpath.core.mapper.MessageDTOMapper;
import mindpath.core.repository.MessageRepository;
import mindpath.core.service.user.UserEntityService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatServiceImpl implements  ChatService{

    private final MessageRepository messageRepository;
    private final UserEntityService userEntityService;
    private final MessageDTOMapper messageDTOMapper;

    public ChatServiceImpl(MessageRepository messageRepository, UserEntityService userEntityService, MessageDTOMapper messageDTOMapper) {
        this.messageRepository = messageRepository;
        this.userEntityService = userEntityService;
        this.messageDTOMapper = messageDTOMapper;
    }

    @Override
    public MessageDTO sendMessage(@NotNull final MessageDTO messageDTO) {
        final UserEntity sender = userEntityService.fetchUserByEmail(messageDTO.sender());
        final UserEntity receiver = userEntityService.fetchUserByEmail(messageDTO.receiver());

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(messageDTO.content())
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.saveAndFlush(message);

        return mapToDTO(message);
    }

    @Override
    public List<MessageDTO> getMessages(@NotNull UserDetails userDetails, UUID receiverId) {
        final UserEntity sender = userEntityService.fetchUserByEmail(userDetails.getUsername());
        final UserEntity receiver = userEntityService.fetchUserByUUID(receiverId);

        List<Message> messages = messageRepository.findBySenderAndReceiver(sender.getUsername(), receiver.getUsername());
        return mapToDTO(messages);
    }

    @Override
    public List<UsersWithLastChat> getChatUsers(UserDetails userDetails) {
        final UserEntity currentUser = userEntityService.fetchUserByEmail(userDetails.getUsername());
        List<UserEntity> admins = userEntityService.filterUsers(null, null, AuthenticationRoles.ROLE_ADMIN, null, null);
        System.out.println(admins);

        Set<UserEntity> users = new LinkedHashSet<>(admins);

        if (currentUser instanceof SuperTeacher superTeacher) {
            if(AuthenticationRoles.ROLE_ADMIN.equals(superTeacher.getRole().getName())){
                users.addAll(userEntityService.filterUsers(null, null, null, null, null));
            }
            else if(AuthenticationRoles.ROLE_SUPER_TEACHER.equals(superTeacher.getRole().getName())){
                users.addAll(superTeacher.getTeachers());
                for (Group g : superTeacher.getGroups()) {
                    users.addAll(g.getStudents());
                }

            }
            users.remove(currentUser);

        } else if (currentUser instanceof Teacher teacher) {
            users.addAll(teacher.getSuperTeachers());
            for (SuperTeacher s : teacher.getSuperTeachers()) {
                for (Group g : s.getGroups()) {
                    for (Subject sub : g.getSubjects()) {
                        if (sub.getTeacher().getId().equals(teacher.getId())) {
                            users.addAll(g.getStudents());
                        }
                    }
                }
            }
            users.remove(currentUser);

        } else if (currentUser instanceof Student student) {
            // Add all super teachers and teachers for the Student
            for (Group g : student.getGroups()) {
                users.add(g.getSuperTeacher());
                for (Subject s : g.getSubjects()) {
                    users.add(s.getTeacher());
                }
            }
            users.remove(currentUser);
        }

        List<UsersWithLastChat> usersWithLastChats = new ArrayList<>();
        for (UserEntity user : users) {
            MessageDTO lastChat = findLastMessageBySenderAndReceiver(currentUser.getUsername(), user.getUsername());
            UsersWithLastChat userWithLastChat = UsersWithLastChat.builder()
                    .user(userEntityService.mapper(user))
                    .lastChat(lastChat)
                    .build();
            usersWithLastChats.add(userWithLastChat);
        }
        usersWithLastChats.sort(
                Comparator.comparing((UsersWithLastChat u) ->
                                u.getLastChat() == null ? null : u.getLastChat().timestamp(),
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        return usersWithLastChats;
    }

    @Override
    public MessageDTO findLastMessageBySenderAndReceiver(final String senderEmail, final String receiverEmail){
        Message lastMessage = messageRepository.findLastMessageBySenderAndReceiver(senderEmail, receiverEmail).orElse(null);
        return lastMessage != null ? mapToDTO(lastMessage) : null;
    }



    private MessageDTO mapToDTO(Message message){
        return messageDTOMapper.apply(message);
    }

    private List<MessageDTO> mapToDTO(@NotNull List<Message> messages){
        return messages.stream().map(messageDTOMapper).toList();
    }
}
