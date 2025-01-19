package mindpath.core.domain.auth.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LogInDTO {

    @Email
    private String email;
    @Pattern(
            message = "Les mots de passe doivent comporter au moins 8 caractères et inclure au moins une lettre majuscule, " +
                    "une lettre minuscule, un chiffre et un caractère spécial.",
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$")
    private String password;
}
