package mindpath.core.domain.token.confirmation;


import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.TokenFactory;
import mindpath.core.domain.token.TokenGenerator;

public class ConfirmationTokenFactory extends TokenFactory<ConfirmationToken> {

    @Override
    protected ConfirmationToken create(UserEntity userEntity) {
        return ConfirmationToken
                .builder()
                .token(getTokenGenerator().generateToken(userEntity))
                .expired(false)
                .revoked(false)
                .userEntity(userEntity)
                .build();
    }

    @Override
    protected TokenGenerator getTokenGenerator() {
        return new ConfirmationTokenGenerator();
    }
}
