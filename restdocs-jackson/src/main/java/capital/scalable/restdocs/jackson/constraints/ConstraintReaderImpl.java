package capital.scalable.restdocs.jackson.constraints;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ValidatorConstraintResolver;

public class ConstraintReaderImpl implements ConstraintReader {
    public static final List<Class<?>> MANDATORY_VALUE_ANNOTATIONS;

    static {
        MANDATORY_VALUE_ANNOTATIONS = new ArrayList<>();
        MANDATORY_VALUE_ANNOTATIONS.add(NotNull.class);
        MANDATORY_VALUE_ANNOTATIONS.add(NotEmpty.class);
        MANDATORY_VALUE_ANNOTATIONS.add(NotBlank.class);
    }

    @Override
    public boolean isMandatory(Class<?> annotation) {
        return MANDATORY_VALUE_ANNOTATIONS.contains(annotation);
    }

    @Override
    public List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName) {
        ConstraintDescriptions constraints = new ConstraintDescriptions(javaBaseClass,
                new SkippableConstraintResolver(new ValidatorConstraintResolver(),
                        MANDATORY_VALUE_ANNOTATIONS));
        return constraints.descriptionsForProperty(javaFieldName);
    }

    @Override
    public List<String> getConstraintMessages(MethodParameter param) {
        return new ArrayList<>(); // TODO method parameter constraints
    }
}
