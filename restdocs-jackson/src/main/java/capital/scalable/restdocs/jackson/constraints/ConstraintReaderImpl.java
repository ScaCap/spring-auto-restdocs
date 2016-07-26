package capital.scalable.restdocs.jackson.constraints;

import static org.apache.commons.lang3.ArrayUtils.contains;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ConstraintResolver;
import org.springframework.restdocs.constraints.ValidatorConstraintResolver;

public class ConstraintReaderImpl implements ConstraintReader {
    // TODO support for composed constraints
    public static final Class<?>[] MANDATORY_VALUE_ANNOTATIONS =
            {NotNull.class, NotEmpty.class, NotBlank.class};

    private ConstraintResolver constraintResolver =
            new HumanReadableConstraintResolver(
                    new SkippableConstraintResolver(new ValidatorConstraintResolver(),
                            MANDATORY_VALUE_ANNOTATIONS));

    @Override
    public boolean isMandatory(Class<?> annotation) {
        return contains(MANDATORY_VALUE_ANNOTATIONS, annotation);
    }

    @Override
    public List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName) {
        ConstraintDescriptions constraints = new ConstraintDescriptions(javaBaseClass,
                constraintResolver);
        return constraints.descriptionsForProperty(javaFieldName);
    }

    @Override
    public List<String> getConstraintMessages(MethodParameter param) {
        return new ArrayList<>(); // TODO method parameter constraints
    }
}
