package mindpath.core.repository.token;

import mindpath.core.domain.token.refresh.RefreshToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface RefreshTokenRepository extends TokenRepository<RefreshToken> {
}
