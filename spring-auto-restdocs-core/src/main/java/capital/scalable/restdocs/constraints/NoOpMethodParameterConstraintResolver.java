package capital.scalable.restdocs.constraints;

import java.util.Collections;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;

public class NoOpMethodParameterConstraintResolver implements MethodParameterConstraintResolver {
    @Override
    public List<Constraint> resolveForParameter(MethodParameter param) {
        return Collections.emptyList();
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        return Collections.emptyList();
    }
}
