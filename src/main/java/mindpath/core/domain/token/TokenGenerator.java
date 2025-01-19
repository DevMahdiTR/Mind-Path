package mindpath.core.domain.token;


import mindpath.core.domain.auth.user.UserEntity;

public interface TokenGenerator {
    String generateToken(UserEntity userEntity);
}
