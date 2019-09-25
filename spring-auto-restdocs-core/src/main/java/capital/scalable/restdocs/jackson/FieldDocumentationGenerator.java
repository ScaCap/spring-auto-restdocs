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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.springframework.restdocs.payload.FieldDescriptor;

public class FieldDocumentationGenerator {
    private static final Logger log = getLogger(FieldDocumentationGenerator.class);
    private static final Set<String> RESOURCES_TYPES = new HashSet<>(Arrays.asList(
            "org.springframework.hateoas.Resources",
            "org.springframework.hateoas.CollectionModel"
    ));

    private final ObjectWriter writer;
    private final DeserializationConfig deserializationConfig;
    private final JavadocReader javadocReader;
    private final ConstraintReader constraintReader;
    private final TypeMapping typeMapping;
    private final SnippetTranslationResolver translationResolver;

    public FieldDocumentationGenerator(ObjectWriter writer,
                                       DeserializationConfig deserializationConfig,
                                       JavadocReader javadocReader,
                                       ConstraintReader constraintReader,
                                       TypeMapping typeMapping, SnippetTranslationResolver translationResolver) {
        this.writer = writer;
        this.deserializationConfig = deserializationConfig;
        this.javadocReader = javadocReader;
        this.constraintReader = constraintReader;
        this.typeMapping = typeMapping;
        this.translationResolver = translationResolver;
    }

    public FieldDescriptors generateDocumentation(Type baseType, TypeFactory typeFactory)
            throws JsonMappingException {
        JavaType javaBaseType = typeFactory.constructType(baseType);
        List<JavaType> types = resolveAllTypes(javaBaseType, typeFactory, typeMapping);
        FieldDescriptors result = new FieldDescriptors();

        FieldDocumentationVisitorWrapper visitorWrapper = FieldDocumentationVisitorWrapper.create(
                javadocReader, constraintReader, deserializationConfig,
                new TypeRegistry(typeMapping, types), typeFactory, translationResolver);

        for (JavaType type : types) {
            log.debug("(TOP) {}", type.getRawClass().getSimpleName());
            if (RESOURCES_TYPES.contains(type.getRawClass().getCanonicalName())) {
                result.setNoContentMessageKey("body-as-embedded-resources");
                continue;
            }
            writer.acceptJsonFormatVisitor(type, visitorWrapper);
        }

        for (FieldDescriptor descriptor : visitorWrapper.getFields()) {
            result.putIfAbsent(descriptor.getPath(), descriptor);
        }
        return result;
    }
}
