package mindpath.core.domain.token.access;


import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.TokenFactory;
import mindpath.core.domain.token.TokenGenerator;

public class AccessTokenFactory extends TokenFactory<AccessToken> {
    @Override
    protected AccessToken create(UserEntity userEntity) {
        return AccessToken.
                builder()
                .token(getTokenGenerator().generateToken(userEntity))
                .expired(false)
                .revoked(false)
                .userEntity(userEntity)
                .build();

    }

    @Override
    protected TokenGenerator getTokenGenerator() {
        return new AccessTokenGenerator();
    }
}
