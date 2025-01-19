package mindpath.core.domain.message;

import mindpath.core.domain.auth.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class UsersWithLastChat {

    private UserDTO user;
    private MessageDTO lastChat;
}
