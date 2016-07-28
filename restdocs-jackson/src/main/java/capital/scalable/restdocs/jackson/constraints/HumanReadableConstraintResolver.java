package capital.scalable.restdocs.jackson.constraints;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.arrayToDelimitedString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

class HumanReadableConstraintResolver implements ConstraintResolver {
    private static final Logger log = getLogger(HumanReadableConstraintResolver.class);

    private final ConstraintResolver delegate;

    public HumanReadableConstraintResolver(ConstraintResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        List<Constraint> result = new ArrayList<>();
        for (Constraint constraint : delegate.resolveForProperty(property, clazz)) {
            result.add(new Constraint(constraint.getName(),
                    extendConfiguration(constraint.getConfiguration())));
        }
        return result;
    }

    private Map<String, Object> extendConfiguration(Map<String, Object> configuration) {
        Map<String, Object> extendedConfiguration = new HashMap<>();
        for (Map.Entry<String, Object> e : configuration.entrySet()) {
            extendedConfiguration.put(e.getKey(), humanReadableString(e.getValue()));
        }
        return extendedConfiguration;
    }

    private String humanReadableString(Object o) {
        if (o instanceof Object[]) {
            return "[" + arrayToDelimitedString((Object[]) o, ", ") + "]";
        } else if (o instanceof Class) {
            try {
                return ((Class) o).newInstance().toString();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to create an instance of {}", ((Class) o).getCanonicalName(), e);
                return "Failed to create an instance of " + ((Class) o).getCanonicalName() +
                        ". Does the class have a no args constructor?";
            }
        } else {
            return o.toString();
        }
    }
}
