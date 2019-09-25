/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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

import static capital.scalable.restdocs.util.TypeUtil.resolveAllTypes;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.springframework.util.Assert;

class FieldDocumentationObjectVisitor extends JsonObjectFormatVisitor.Base {
    private static final Logger log = getLogger(FieldDocumentationObjectVisitor.class);

    private final static Set<String> SKIPPED_FIELDS = new HashSet<>(Arrays.asList(
            "_links", "_embedded"
    ));
    private final static Set<String> SKIPPED_CLASSES = new HashSet<>(Arrays.asList(
            "org.springframework.hateoas.Link",
            "org.springframework.hateoas.Links",
            "org.springframework.hateoas.core.EmbeddedWrapper",
            "org.springframework.hateoas.server.core.EmbeddedWrapper"
    ));

    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final TypeRegistry typeRegistry;
    private final TypeFactory typeFactory;

    public FieldDocumentationObjectVisitor(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, TypeRegistry typeRegistry,
            TypeFactory typeFactory) {
        super(provider);
        this.context = context;
        this.path = path;
        this.typeRegistry = typeRegistry;
        this.typeFactory = typeFactory;
    }

    /**
     * Called for required bean properties like fields/properties annotated with @JsonProperty(required = true)
     * or non-null Kotlin properties.
     */
    @Override
    public void property(BeanProperty prop) throws JsonMappingException {
        property(prop, true);
    }

    /**
     * Called for all non-required bean properties like all not annotated Java fields
     * or not annotated nullable Kotlin properties.
     */
    @Override
    public void optionalProperty(BeanProperty prop) throws JsonMappingException {
        property(prop, false);
    }

    public void property(BeanProperty prop, boolean required) throws JsonMappingException {
        String jsonName = prop.getName();
        String fieldName = prop.getMember().getName();

        JavaType baseType = prop.getType();
        Assert.notNull(baseType,
                "Missing type for property '" + jsonName + "', field '" + fieldName + "'");

        for (JavaType javaType : resolveAllTypes(baseType, typeFactory, typeRegistry.getTypeMapping())) {
            JsonSerializer<?> ser = getProvider().findValueSerializer(javaType, prop);
            if (ser == null) {
                return;
            }
            if (shouldSkip(prop)) {
                return;
            }


            visitType(prop, jsonName, fieldName, javaType, ser, required);
        }
    }

    private void visitType(BeanProperty prop, String jsonName, String fieldName, JavaType fieldType,
            JsonSerializer<?> ser, boolean required) throws JsonMappingException {
        String fieldPath = path + (path.isEmpty() ? "" : ".") + jsonName;
        log.debug("({}) {}", fieldPath, fieldType.getRawClass().getSimpleName());
        Class<?> javaBaseClass = prop.getMember().getDeclaringClass();
        boolean shouldExpand = shouldExpand(prop);

        InternalFieldInfo fieldInfo = new InternalFieldInfo(javaBaseClass, fieldName, fieldType,
                fieldPath, shouldExpand, required);

        JsonFormatVisitorWrapper visitor = new FieldDocumentationVisitorWrapper(getProvider(),
                context, fieldPath, fieldInfo, typeRegistry, typeFactory);

        ser.acceptJsonFormatVisitor(visitor, fieldType);
    }

    private boolean shouldExpand(BeanProperty prop) {
        return prop.getMember().getAnnotation(RestdocsNotExpanded.class) == null;
    }

    private boolean shouldSkip(BeanProperty prop) {
        Class<?> rawClass = prop.getType().getContentType() != null
                ? prop.getType().getContentType().getRawClass()
                : prop.getType().getRawClass();
        return SKIPPED_FIELDS.contains(prop.getName())
                || SKIPPED_CLASSES.contains(rawClass.getCanonicalName());
    }
}
