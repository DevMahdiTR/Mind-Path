package mindpath.core.service.auth.regiter;

import mindpath.config.APIRouters;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.confirmation.ConfirmationToken;
import mindpath.core.domain.token.confirmation.ConfirmationTokenFactory;
import mindpath.core.domain.token.refresh.RefreshToken;
import mindpath.core.domain.token.refresh.RefreshTokenFactory;
import mindpath.core.exceptions.custom.EmailRegisteredException;
import mindpath.core.service.email.EmailService;
import mindpath.core.service.role.RoleService;
import mindpath.core.service.token.TokenService;
import mindpath.core.service.user.UserEntityService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public abstract class UserRegistration<T extends UserEntity> {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TokenService<RefreshToken> refreshTokenTokenService;
    @Autowired
    private  TokenService<ConfirmationToken> confirmationTokenTokenService;
    @Autowired
    private EmailService emailService;

    protected abstract T createUser(RegisterUserDTO registerUserDTO);
    protected abstract String getRole();

    @Transactional(rollbackFor = Exception.class)
    public String register(@NotNull RegisterUserDTO registerUserDTO) {
        log.debug("Attempting to register user with email: {}", registerUserDTO.getEmail());

        if (userEntityService.isEmailRegistered(registerUserDTO.getEmail())) {
            log.warn("Email déjà enregistré : {}", registerUserDTO.getEmail());
            throw new EmailRegisteredException("Email déjà utilisé.");
        }

        T newUser = createUser(registerUserDTO);
        newUser.setRole(roleService.fetchRoleByName(getRole()));
        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setEnabled(false);

        UserEntity savedUser = userEntityService.save(newUser);

        RefreshToken refreshToken = new RefreshTokenFactory().build(savedUser);
        ConfirmationToken confirmationToken = new ConfirmationTokenFactory().build(savedUser);
        confirmationTokenTokenService.saveAndFlush(confirmationToken);
        refreshTokenTokenService.saveAndFlush(refreshToken);

        String activationLink = APIRouters.getConfirmationURL(confirmationToken.getToken());

        log.debug("Sending activation email to: {}", registerUserDTO.getEmail());
        emailService.sendMail(
                registerUserDTO.getEmail(),
                "Activating your account.",
                "email-confirmation",
                Map.of(
                        "name", registerUserDTO.getEmail(),
                        "link", activationLink
                )
        );

        log.info("User registered successfully: {}", registerUserDTO.getEmail());
        return "User registered successfully";
    }
}