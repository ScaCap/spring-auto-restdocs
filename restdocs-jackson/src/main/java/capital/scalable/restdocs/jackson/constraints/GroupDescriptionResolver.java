package capital.scalable.restdocs.jackson.constraints;

import java.util.List;

import org.springframework.restdocs.constraints.Constraint;

public interface GroupDescriptionResolver {
    List<Class> getGroups(Constraint constraint);

    String resolveGroupDescription(Class group, String constraintDescription);
}
