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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Florian Benz
 */
public class FieldDocumentationObjectVisitor extends JsonObjectFormatVisitor.Base {

    private final Class<?> javaBaseClass;
    private final FieldDocumentationVisitorContext context;
    private final String path;

    public FieldDocumentationObjectVisitor(SerializerProvider provider, Class<?> javaBaseClass,
            FieldDocumentationVisitorContext context, String path) {
        super(provider);
        this.javaBaseClass = javaBaseClass;
        this.context = context;
        this.path = path;
    }

    @Override
    public void optionalProperty(BeanProperty prop) throws JsonMappingException {
        JavaType type = prop.getType();
        if (type == null) {
            throw new IllegalStateException("Missing type for property '" + prop.getName() + "'");
        }

        JsonSerializer<?> ser = getSer(prop);
        if (ser == null) {
            return;
        }

        String fieldPath = path + (path.isEmpty() ? "" : ".") + prop.getName();
        InternalFieldInfo fieldInfo =
                new InternalFieldInfo(javaBaseClass, prop.getName(), fieldPath, isOptional(prop));
        JsonFormatVisitorWrapper visitor =
                new FieldDocumentationVisitorWrapper(getProvider(), context, fieldPath, fieldInfo);

        ser.acceptJsonFormatVisitor(visitor, type);
    }

    private boolean isOptional(BeanProperty prop) {
        return prop.getAnnotation(NotNull.class) == null
                && prop.getAnnotation(NotEmpty.class) == null
                && prop.getAnnotation(NotBlank.class) == null;
    }

    protected JsonSerializer<?> getSer(BeanProperty prop) throws JsonMappingException {
        JsonSerializer<Object> ser = null;
        if (prop instanceof BeanPropertyWriter) {
            ser = ((BeanPropertyWriter) prop).getSerializer();
        }
        if (ser == null) {
            ser = getProvider().findValueSerializer(prop.getType(), prop);
        }
        return ser;
    }
}