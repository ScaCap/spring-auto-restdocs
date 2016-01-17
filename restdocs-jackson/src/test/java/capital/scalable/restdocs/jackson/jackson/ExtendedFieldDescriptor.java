package capital.scalable.restdocs.jackson.jackson;

import org.springframework.restdocs.payload.FieldDescriptor;

/**
 * Same fields as {@link FieldDescriptor}, but with {@link #equals(Object)} and {@link #toString()}
 * implemented to simplify testing.
 */
public class ExtendedFieldDescriptor {

    private final String path;

    private final Object type;

    private final boolean optional;

    private final Object description;

    public ExtendedFieldDescriptor(FieldDescriptor descriptor) {
        this.path = descriptor.getPath();
        this.type = descriptor.getType();
        this.optional = descriptor.isOptional();
        this.description = descriptor.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExtendedFieldDescriptor that = (ExtendedFieldDescriptor) o;

        if (optional != that.optional) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return description != null ? description.equals(that.description) :
                that.description == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (optional ? 1 : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExtendedFieldDescriptor{" +
                "path='" + path + '\'' +
                ", type=" + type +
                ", optional=" + optional +
                ", description=" + description +
                '}';
    }
}
