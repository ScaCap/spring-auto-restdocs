package capital.scalable.restdocs.jackson.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class OneOfValidator implements ConstraintValidator<OneOf, String> {

    private List<String> validValues;

    @Override
    public void initialize(OneOf constraintAnnotation) {
        validValues = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else {
            return validValues.contains(value);
        }
    }
}
