package mindpath.core.repository.token;

import mindpath.core.domain.token.access.AccessToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AccessTokenRepository extends TokenRepository<AccessToken> {

}
