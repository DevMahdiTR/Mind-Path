package mindpath.core.domain.token.refresh;


import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.TokenFactory;
import mindpath.core.domain.token.TokenGenerator;

public class RefreshTokenFactory extends TokenFactory<RefreshToken> {


    @Override
    protected RefreshToken create(UserEntity userEntity) {
        return RefreshToken
                .builder()
                .token(getTokenGenerator().generateToken(userEntity))
                .expired(false)
                .revoked(false)
                .userEntity(userEntity)
                .build();
    }

    @Override
    protected TokenGenerator getTokenGenerator() {
        return new RefreshTokenGenerator();
    }
}
