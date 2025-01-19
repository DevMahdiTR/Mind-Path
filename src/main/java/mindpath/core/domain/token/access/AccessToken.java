package mindpath.core.domain.token.access;

import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;



@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "access_tokens")
public class AccessToken extends Token {

    public AccessToken(Long id, String token, boolean revoked, boolean expired, UserEntity userEntity) {
        super(id, token, revoked, expired, userEntity);
    }

    public static AccessTokenBuilder builder() {
        return new AccessTokenBuilder();
    }


}
