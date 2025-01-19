package mindpath.core.service.auth;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.PasswordReset;
import mindpath.core.domain.auth.login.LogInDTO;
import mindpath.core.domain.auth.login.LogInResponseDTO;
import mindpath.core.domain.auth.register.RegisterStudentDTO;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.access.AccessToken;
import mindpath.core.domain.token.access.AccessTokenFactory;
import mindpath.core.domain.token.confirmation.ConfirmationToken;
import mindpath.core.domain.token.refresh.RefreshToken;
import mindpath.core.domain.token.refresh.RefreshTokenFactory;
import mindpath.core.exceptions.custom.EmailRegisteredException;
import mindpath.core.exceptions.custom.ExpiredTokenException;
import mindpath.core.exceptions.custom.InvalidTokenException;
import mindpath.core.service.auth.regiter.StudentRegistration;
import mindpath.core.service.auth.regiter.SuperTeacherRegistration;
import mindpath.core.service.auth.regiter.TeacherRegistration;
import mindpath.core.service.email.EmailService;
import mindpath.core.service.passwordreset.PasswordResetService;
import mindpath.core.service.role.RoleService;
import mindpath.core.service.token.TokenService;
import mindpath.core.service.user.UserEntityService;
import mindpath.core.utility.RandomCodeGenerator;
import mindpath.core.utility.ThymeleafUtil;
import mindpath.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserEntityService userEntityService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtTokenProvider jwtTokenProvider;

    private final TokenService<AccessToken> accessTokenTokenService;
    private final TokenService<RefreshToken> refreshTokenTokenService;
    private final TokenService<ConfirmationToken> confirmationTokenTokenService;

    private final SuperTeacherRegistration superTeacherRegistration;
    private final TeacherRegistration teacherRegistration;
    private final StudentRegistration studentRegistration;

    private final PasswordResetService passwordResetService;

    @Override
    public String registerTeachers(@NotNull String roleName, @NotNull RegisterUserDTO registerUserDTO) {
        return switch (roleName) {
            case AuthenticationRoles.ROLE_SUPER_TEACHER -> superTeacherRegistration.register(registerUserDTO);
            case AuthenticationRoles.ROLE_TEACHER -> teacherRegistration.register(registerUserDTO);
            default -> throw new IllegalArgumentException("Rôle invalide");
        };
    }
    @Override
    public String registerStudents(@NotNull RegisterStudentDTO registerStudentDTO) {
        return studentRegistration.register(registerStudentDTO);
    }


    @Override
    public LogInResponseDTO logIn(@NotNull LogInDTO logInDTO) {
        log.debug("Attempting to log in user with email: {}", logInDTO.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        logInDTO.getEmail(),
                        logInDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userEntityService.fetchUserByEmail(logInDTO.getEmail());


        accessTokenTokenService.revokeAllUserToken(user);
        refreshTokenTokenService.revokeAllUserToken(user);

        AccessToken accessToken = new AccessTokenFactory().build(user);
        RefreshToken refreshToken = new RefreshTokenFactory().build(user);
        accessTokenTokenService.save(accessToken);
        refreshTokenTokenService.save(refreshToken);

        final LogInResponseDTO logInResponseDTO = LogInResponseDTO
                .builder()
                .userEntityDTO(userEntityService.mapper(user))
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();

        log.info("User logged in successfully: {}", logInDTO.getEmail());
        return logInResponseDTO;
    }

    @Override
    public String validateToken(String token) {
        log.debug("Validating token: {}", token);

        final AccessToken accessToken = accessTokenTokenService.findByToken(token);
        final boolean isValid = jwtTokenProvider.validateToken(token);

        if (!isValid) {
            log.warn("Invalid JWT token: {}", token);
            throw  new InvalidTokenException("Token JWT invalide");
        }

        if (accessToken.isExpired() || accessToken.isRevoked()) {
            log.warn("Expired or revoked JWT token: {}", token);
            throw new ExpiredTokenException("Token JWT expiré");
        }

        log.info("JWT token is valid: {}", token);
        return "Token JWT valide";
    }

    @Override
    public String confirmAccount(String confirmationToken) {
        log.debug("Activating account with token: {}", confirmationToken);

        ConfirmationToken existedConfirmationToken = confirmationTokenTokenService.findByToken(confirmationToken);

        if (existedConfirmationToken.isExpired() || existedConfirmationToken.isRevoked()) {
            log.warn("Invalid or expired confirmation token: {}", confirmationToken);
            throw new InvalidTokenException("Token invalide.");
        }

        UserEntity existedUser = existedConfirmationToken.getUserEntity();

        if (existedUser.isEnabled()) {
            log.warn("Account already activated for token: {}", confirmationToken);
            throw new InvalidTokenException("Déjà activé");
        }

        existedUser.setEnabled(true);
        userEntityService.saveAndFlush(existedUser);

        log.info("Account activated successfully for token: {}", confirmationToken);
        return ThymeleafUtil.processEmailTemplate("confirmation-page", Collections.emptyMap());
    }

    @Override
    public String processForgotPassword(String email) {
        log.debug("Processing forgot password for email: {}", email);
        if(!userEntityService.isEmailRegistered(email)){
            throw new EmailRegisteredException("Email non enregistré");
        }

        PasswordReset existedPasswordReset = passwordResetService.fetchValidPasswordReset(email);

        if(existedPasswordReset != null){
            if(existedPasswordReset.getExpiryAt().isAfter(LocalDateTime.now())){
                return "Lien de réinitialisation de mot de passe déjà envoyé à votre email";
            }
        }

        final String verificationCode = RandomCodeGenerator.generateRandomCode();
        final PasswordReset passwordReset = PasswordReset.builder()
                .email(email)
                .token(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiryAt(LocalDateTime.now().plusMinutes(30))
                .build();
        passwordResetService.save(passwordReset);
        emailService.sendMail(
                email,
                "Réinitialisation du mot de passe",
                "password-reset",
                Map.of(
                        "name",email
                        ,"verificationCode",verificationCode
                )

        );
        return "Lien de réinitialisation de mot de passe envoyé à votre email";
    }

    @Override
    public String resetPassword(String token, String email, String password) {
        log.debug("Resetting password for email: {}", email);

        PasswordReset existedPasswordReset = passwordResetService.fetchValidPasswordReset(email);



        if(existedPasswordReset == null){
            log.warn("Invalid password reset token: {}", token);
            throw new InvalidTokenException("Code invalide.");
        }
        if(existedPasswordReset.getExpiryAt() != null && existedPasswordReset.getExpiryAt().isBefore(LocalDateTime.now())){
            log.warn("Expired password reset token: {}", token);
            throw new ExpiredTokenException("Code expiré.");
        }
        if(!existedPasswordReset.getToken().equals(token)){
            log.warn("Invalid password reset token: {}", token);
            throw new InvalidTokenException("Code invalide.");
        }

        UserEntity existedUser = userEntityService.fetchUserByEmail(email);
        existedUser.setPassword(passwordEncoder.encode(password));
        userEntityService.saveAndFlush(existedUser);
        log.error("new password: {}",password);
        passwordResetService.deleteById(existedPasswordReset.getId());

        log.info("Password reset successfully for email: {}", email);
        return "Mot de passe réinitialisé avec succès";
    }

    private boolean isValidPassword(String password) {
        // Regex pattern to check password constraints
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

}
