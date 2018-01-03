package capital.scalable.restdocs.jackson;

import static capital.scalable.restdocs.constraints.ConstraintReader.CONSTRAINTS_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

/**
 * Same fields as {@link FieldDescriptor}, but with {@link #equals(Object)} and {@link #toString()}
 * implemented to simplify testing.
 */
public class ExtendedFieldDescriptor {

    private final String path;

    private final Object type;

    private final List<String> optionals;

    private final Object description;

    private final List<String> constraints;

    public ExtendedFieldDescriptor(FieldDescriptor descriptor) {
        this.path = descriptor.getPath();
        this.type = descriptor.getType();
        this.optionals = (List<String>) descriptor.getAttributes().get(OPTIONAL_ATTRIBUTE);
        this.description = descriptor.getDescription();
        this.constraints = (List<String>) descriptor.getAttributes().get(CONSTRAINTS_ATTRIBUTE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ExtendedFieldDescriptor that = (ExtendedFieldDescriptor) o;

        if (path != null ? !path.equals(that.path) : that.path != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (optionals != null ? !optionals.equals(that.optionals) : that.optionals != null)
            return false;
        return constraints != null ? constraints.equals(that.constraints) :
                that.constraints == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (optionals != null ? optionals.hashCode() : 0);
        result = 31 * result + (constraints != null ? constraints.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExtendedFieldDescriptor{" +
                "path='" + path + '\'' +
                ", type=" + type +
                ", optional=" + optionals +
                ", description=" + description +
                ", constraints=" + constraints +
                '}';
    }
}
