package mindpath.core.service.passwordreset;

import mindpath.core.domain.PasswordReset;

public interface PasswordResetService {

    PasswordReset save(PasswordReset passwordReset);
    void deleteById(long id);
    PasswordReset fetchValidPasswordReset(String email);
}
