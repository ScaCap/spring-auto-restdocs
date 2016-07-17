package capital.scalable.restdocs.jackson.constraints;

import java.util.List;

import org.springframework.core.MethodParameter;

public interface ConstraintReader {
    public static final String CONSTRAINTS_ATTRIBUTE = "constraints";

    boolean isMandatory(Class<?> annotation);

    List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName);

    List<String> getConstraintMessages(MethodParameter param);
}
