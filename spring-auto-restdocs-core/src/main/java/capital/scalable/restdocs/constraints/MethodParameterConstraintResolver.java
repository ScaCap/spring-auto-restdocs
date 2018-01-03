package capital.scalable.restdocs.constraints;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

public interface MethodParameterConstraintResolver extends ConstraintResolver {
    List<Constraint> resolveForParameter(MethodParameter param);
}

