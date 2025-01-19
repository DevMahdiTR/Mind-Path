package mindpath.core.service.passwordreset;

import mindpath.core.domain.PasswordReset;
import mindpath.core.repository.PasswordResetRepository;
import org.springframework.stereotype.Service;


@Service
public class PasswordResetServiceImpl implements PasswordResetService{

    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetServiceImpl(PasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    @Override
    public PasswordReset save(PasswordReset passwordReset) {
        return passwordResetRepository.save(passwordReset);
    }

    @Override
    public void deleteById(long id) {
        passwordResetRepository.deleteById(id);
    }

    @Override
    public PasswordReset fetchValidPasswordReset(String email) {
        return passwordResetRepository.findByEmailAndToken(email)
                .orElse(null);
    }
}
