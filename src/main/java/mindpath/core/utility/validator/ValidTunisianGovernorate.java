package mindpath.core.utility.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TunisianGovernorateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTunisianGovernorate {
    String message() default "Invalid education level";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
