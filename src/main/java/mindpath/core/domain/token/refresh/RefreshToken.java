package mindpath.core.domain.token.refresh;

import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends Token {

    public RefreshToken(Long id, String token, boolean revoked, boolean expired, UserEntity userEntity) {
        super(id, token, revoked, expired, userEntity);
    }


    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }
}
