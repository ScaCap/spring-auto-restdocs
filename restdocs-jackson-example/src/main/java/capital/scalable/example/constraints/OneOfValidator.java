package capital.scalable.example.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OneOfValidator implements ConstraintValidator<OneOf, String> {
    @Override
    public void initialize(OneOf constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return true;
    }
}
