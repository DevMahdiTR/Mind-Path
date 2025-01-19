package mindpath.core.repository.token;

import mindpath.core.domain.token.confirmation.ConfirmationToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends TokenRepository<ConfirmationToken> {


}
