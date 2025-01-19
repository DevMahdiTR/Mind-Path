package mindpath.core.utility.validator;

import mindpath.core.domain.auth.user.EducationLevel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EducationLevelValidator implements ConstraintValidator<ValidEducationLevel, EducationLevel> {
    @Override
    public boolean isValid(EducationLevel educationLevel, ConstraintValidatorContext context) {
        return educationLevel != null && Arrays.asList(EducationLevel.values()).contains(educationLevel);
    }
}