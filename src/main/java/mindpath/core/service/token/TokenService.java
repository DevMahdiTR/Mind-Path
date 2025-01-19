package mindpath.core.service.token;

import org.jetbrains.annotations.NotNull;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.Token;

import java.util.List;
import java.util.UUID;

public interface TokenService<T extends Token> {

    T saveAndFlush(T token);

    T save(T token);

    List<T> saveAll(final List<T> t);

    List<T> fetchAllValidTokenByUserId(UUID userId);

    T findByToken(String token);

    boolean isTokenValidAndExist(String token);

    void revokeAllUserToken(@NotNull final UserEntity userEntity);



}
