package capital.scalable.restdocs.jackson.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

class SkippableConstraintResolver implements ConstraintResolver {
    private final ConstraintResolver delegate;
    private final Collection<String> skippableConstraints;

    public SkippableConstraintResolver(ConstraintResolver delegate,
            Class<?>... skippableConstraints) {
        this.delegate = delegate;
        this.skippableConstraints = new ArrayList<>();
        for (Class<?> a : skippableConstraints) {
            this.skippableConstraints.add(a.getCanonicalName());
        }
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        List<Constraint> result = new ArrayList<>();
        for (Constraint constraint : delegate.resolveForProperty(property, clazz)) {
            if (isSkippable(constraint))
                continue;
            result.add(constraint);
        }
        return result;
    }

    private boolean isSkippable(Constraint constraint) {
        return skippableConstraints.contains(constraint.getName());
    }
}
