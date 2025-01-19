package mindpath.core.domain.auth.register;

import mindpath.core.domain.auth.user.EducationLevel;
import mindpath.core.utility.validator.ValidEducationLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterStudentDTO extends RegisterUserDTO {

    @ValidEducationLevel(message = "Invalid education level")
    @NotNull(message = "Education level is required")
    private EducationLevel educationLevel;
}
