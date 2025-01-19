package mindpath.core.domain.token.confirmation;

import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationToken extends Token {

    public ConfirmationToken(Long id, String token, boolean revoked, boolean expired, UserEntity userEntity) {
        super(id, token, revoked, expired, userEntity);
    }

    public static ConfirmationTokenBuilder builder() {
        return new ConfirmationTokenBuilder();
    }
}
