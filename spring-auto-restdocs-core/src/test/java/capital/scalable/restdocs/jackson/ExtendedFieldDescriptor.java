/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
 * %%
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
 * #L%
 */
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
