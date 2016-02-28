/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
