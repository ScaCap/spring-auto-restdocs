package capital.scalable.restdocs.constraints;

import java.util.List;

import org.springframework.core.MethodParameter;

public interface ConstraintReader {
    String CONSTRAINTS_ATTRIBUTE = "constraints";
    String OPTIONAL_ATTRIBUTE = "optionals";
    String DEPRECATED_ATTRIBUTE = "deprecated";
    String DEFAULT_VALUE_ATTRIBUTE = "default-value";

    List<String> getConstraintMessages(Class<?> javaBaseClass, String javaFieldName);

    List<String> getConstraintMessages(MethodParameter param);

    List<String> getOptionalMessages(Class<?> javaBaseClass, String javaFieldName);
}
