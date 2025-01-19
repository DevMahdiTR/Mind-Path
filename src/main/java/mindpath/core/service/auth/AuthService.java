package mindpath.core.service.auth;


import mindpath.core.domain.auth.login.LogInDTO;
import mindpath.core.domain.auth.login.LogInResponseDTO;
import mindpath.core.domain.auth.register.RegisterStudentDTO;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import org.jetbrains.annotations.NotNull;

public interface AuthService {

    String registerTeachers(@NotNull final String roleName, @NotNull final RegisterUserDTO registerUserDTO);
    String registerStudents(@NotNull final RegisterStudentDTO registerStudentDTO);
    LogInResponseDTO logIn(@NotNull final LogInDTO logInDTO);
    String validateToken(final String token);
    String confirmAccount(final String confirmationToken);
    String processForgotPassword(final String email);
    String resetPassword(final String token, final String email,final String password);

}
