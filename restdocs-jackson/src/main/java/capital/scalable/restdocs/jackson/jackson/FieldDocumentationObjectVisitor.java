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

import static org.slf4j.LoggerFactory.getLogger;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import capital.scalable.restdocs.jackson.validation.DocumentedClass;
import capital.scalable.restdocs.jackson.validation.DocumentedConstraint;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;

/**
 * @author Florian Benz
 */
public class FieldDocumentationObjectVisitor extends JsonObjectFormatVisitor.Base {

    private static final Logger log = getLogger(FieldDocumentationObjectVisitor.class);

    private final FieldDocumentationVisitorContext context;
    private final String path;

    public FieldDocumentationObjectVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path) {
        super(provider);
        this.context = context;
        this.path = path;
    }

    @Override
    public void optionalProperty(BeanProperty prop) throws JsonMappingException {
        String jsonName = prop.getName();
        String fieldName = prop.getMember().getName();

        JavaType type = prop.getType();
        if (type == null) {
            throw new IllegalStateException("Missing type for property '" + jsonName + "', " +
                    "field '" + fieldName + "'");
        }

        JsonSerializer<?> ser = getSer(prop);
        if (ser == null) {
            return;
        }

        String fieldPath = path + (path.isEmpty() ? "" : ".") + jsonName;
        Class<?> javaBaseClass = prop.getMember().getDeclaringClass();
        String validationInformation = documentationFromValidationConstraints(prop);

        InternalFieldInfo fieldInfo =
                new InternalFieldInfo(javaBaseClass, fieldName, fieldPath, isOptional(prop),
                        validationInformation);

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

    private String documentationFromValidationConstraints(BeanProperty prop) {
        List<String> info = new ArrayList<>();
        Annotation[] annotations = prop.getMember().getAnnotated().getDeclaredAnnotations();
        for (Annotation constraint : annotations) {
            String documentationMethodName = documentationMethodName(constraint);
            if (documentationMethodName != null) {
                info.add(documentationFromAnnotation(constraint, documentationMethodName));
            }
        }
        return StringUtils.join(info, "; ");
    }

    private String documentationMethodName(Annotation constraint) {
        Annotation[] annotationsOnAnnotation = constraint.annotationType().getDeclaredAnnotations();
        for (Annotation documentationInformation : annotationsOnAnnotation) {
            if (DocumentedConstraint.class.equals(documentationInformation.annotationType())) {
                return ((DocumentedConstraint) documentationInformation).documentationMethod();
            }
        }
        return null;
    }

    private String documentationFromAnnotation(Annotation constraint,
            String documentationMethodName) {
        try {
            Method documentationMethod = constraint.getClass().getMethod(documentationMethodName);
            Object result = documentationMethod.invoke(constraint);
            if (Class.class.equals(documentationMethod.getReturnType())) {
                Class<? extends DocumentedClass> x = (Class<? extends DocumentedClass>) result;
                return x.newInstance().documentation();
            } else {
                return (String) result;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException
                | InstantiationException e) {
            log.error("Failed to extract validation documentation from constraint", e);
            return "<error>";
        }
    }
}
