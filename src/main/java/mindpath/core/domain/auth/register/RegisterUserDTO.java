package mindpath.core.domain.auth.register;


import mindpath.core.domain.auth.user.TunisianGovernorate;
import mindpath.core.utility.validator.ValidTunisianGovernorate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
public class RegisterUserDTO {

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(min = 5, max = 50, message = "Le nom complet doit comporter entre 5 et 50 caractères")
    private String fullName;

    @Pattern(regexp = "\\d{8}", message = "Le numéro de téléphone doit comporter 8 chiffres")
    private String phoneNumber;

    @ValidTunisianGovernorate(message = "Gouvernorat invalide")
    @NotNull(message = "Le gouvernorat est obligatoire")
    private TunisianGovernorate governorate;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthday;

    @NotBlank(message = "L'adresse e-mail est obligatoire")
    @Email(message = "Format d'e-mail invalide")
    private String email;

    @Pattern(
            message = "Les mots de passe doivent comporter au moins 8 caractères et inclure au moins une lettre majuscule, " +
                    "une lettre minuscule, un chiffre et un caractère spécial.",
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$")
    private String password;

}
