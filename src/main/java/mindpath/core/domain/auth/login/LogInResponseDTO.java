package mindpath.core.domain.auth.login;

import mindpath.core.domain.auth.user.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogInResponseDTO {
    private UserDTO userEntityDTO;
    private String accessToken;
    private String refreshToken;
}
