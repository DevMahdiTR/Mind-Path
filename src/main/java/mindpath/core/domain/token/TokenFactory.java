package mindpath.core.domain.token;


import mindpath.core.domain.auth.user.UserEntity;

public abstract class TokenFactory<T extends Token> {

    protected abstract T create(UserEntity userEntity);

    protected abstract TokenGenerator getTokenGenerator();

    public T build(UserEntity userEntity) {
        return create(userEntity);
    }
}
