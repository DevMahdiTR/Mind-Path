package mindpath.core.utility.validator;

import mindpath.core.domain.auth.user.TunisianGovernorate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class TunisianGovernorateValidator implements ConstraintValidator<ValidTunisianGovernorate, TunisianGovernorate> {
    @Override
    public void initialize(ValidTunisianGovernorate constraintAnnotation) {}

    @Override
    public boolean isValid(TunisianGovernorate tunisianGovernorate, ConstraintValidatorContext constraintValidatorContext) {
        return tunisianGovernorate != null && Arrays.asList(TunisianGovernorate.values()).contains(tunisianGovernorate);
    }
}
