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

package capital.scalable.restdocs.jackson;

import java.lang.reflect.Type;
import java.util.List;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.restdocs.payload.FieldDescriptor;

public class FieldDocumentationGenerator {

    private final ObjectWriter writer;
    private final DeserializationConfig deserializationConfig;
    private final JavadocReader javadocReader;
    private final ConstraintReader constraintReader;

    public FieldDocumentationGenerator(ObjectWriter writer,
            DeserializationConfig deserializationConfig,
            JavadocReader javadocReader,
            ConstraintReader constraintReader) {
        this.writer = writer;
        this.deserializationConfig = deserializationConfig;
        this.javadocReader = javadocReader;
        this.constraintReader = constraintReader;
    }

    public List<FieldDescriptor> generateDocumentation(Type type, TypeFactory typeFactory)
            throws JsonMappingException {
        return generateDocumentation(typeFactory.constructType(type));
    }

    public List<FieldDescriptor> generateDocumentation(JavaType type) throws JsonMappingException {
        FieldDocumentationVisitorWrapper visitorWrapper = FieldDocumentationVisitorWrapper.create(
                javadocReader, constraintReader, deserializationConfig);
        writer.acceptJsonFormatVisitor(type, visitorWrapper);
        return visitorWrapper.getContext().getFields();
    }
}
