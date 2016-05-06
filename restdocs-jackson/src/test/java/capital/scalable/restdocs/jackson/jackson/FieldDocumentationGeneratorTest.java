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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import capital.scalable.restdocs.jackson.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

public class FieldDocumentationGeneratorTest {

    @Test
    public void testGenerateDocumentationForPrimitiveTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(PrimitiveTypes.class, "stringField"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(PrimitiveTypes.class, "booleanField"))
                .thenReturn("A boolean");
        when(javadocReader.resolveFieldComment(PrimitiveTypes.class, "numberField1"))
                .thenReturn("An integer");
        when(javadocReader.resolveFieldComment(PrimitiveTypes.class, "numberField2"))
                .thenReturn("A decimal");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), javadocReader);
        Type type = PrimitiveTypes.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(fieldDescriptions.get(0),
                is(descriptor("stringField", "String", "A string", false)));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("booleanField", "Boolean", "A boolean", true)));
        assertThat(fieldDescriptions.get(2),
                is(descriptor("numberField1", "Integer", "An integer", false)));
        assertThat(fieldDescriptions.get(3),
                is(descriptor("numberField2", "Decimal", "A decimal", true)));
    }

    @Test
    public void testGenerateDocumentationForComposedTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(ComposedTypes.class, "objectField"))
                .thenReturn("An object");
        when(javadocReader.resolveFieldComment(PrimitiveTypes.class, "stringField"))
                .thenReturn("A string");
        when(javadocReader.resolveFieldComment(ComposedTypes.class, "arrayField"))
                .thenReturn("An array");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), javadocReader);
        Type type = ComposedTypes.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(fieldDescriptions.size(), is(6));
        assertThat(fieldDescriptions.get(0),
                is(descriptor("objectField", "Object", "An object", false)));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("objectField.stringField", "String", "A string", false)));
        assertThat(fieldDescriptions.get(5),
                is(descriptor("arrayField", "Array", "An array", true)));
    }

    @Test
    public void testGenerateDocumentationForNestedTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(FirstLevel.class, "second"))
                .thenReturn("2nd level");
        when(javadocReader.resolveFieldComment(SecondLevel.class, "third"))
                .thenReturn("3rd level");
        when(javadocReader.resolveFieldComment(ThirdLevel.class, "fourth"))
                .thenReturn("4th level");
        when(javadocReader.resolveFieldComment(FourthLevel.class, "fifth"))
                .thenReturn("5th level");
        when(javadocReader.resolveFieldComment(FifthLevel.class, "last"))
                .thenReturn("An integer");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), javadocReader);
        Type type = FirstLevel.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(fieldDescriptions.get(0),
                is(descriptor("second", "Object", "2nd level", true)));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("second.third", "Array", "3rd level", false)));
        assertThat(fieldDescriptions.get(2),
                is(descriptor("second.third[].fourth", "Object", "4th level", true)));
        assertThat(fieldDescriptions.get(3),
                is(descriptor("second.third[].fourth.fifth", "Array", "5th level", true)));
        assertThat(fieldDescriptions.get(4),
                is(descriptor("second.third[].fourth.fifth[].last", "Integer", "An integer",
                        true)));
    }

    @Test
    public void testGenerateDocumentationForExternalSerializer() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(ExternalSerializer.class, "bigDecimal"))
                .thenReturn("A decimal");

        SimpleModule testModule = new SimpleModule("TestModule");
        testModule.addSerializer(new BigDecimalSerializer());
        mapper.registerModule(testModule);

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), javadocReader);
        Type type = ExternalSerializer.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(fieldDescriptions.get(0),
                is(descriptor("bigDecimal", "Decimal", "A decimal", true)));
    }

    @Test
    public void testGenerateDocumentationForJacksonAnnotations() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(JsonAnnotations.class, "location"))
                .thenReturn("A location");
        when(javadocReader.resolveFieldComment(JsonAnnotations.class, "uri"))
                .thenReturn("A uri");
        when(javadocReader.resolveMethodComment(JsonAnnotations.class, "getParameter"))
                .thenReturn("A parameter");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), javadocReader);
        Type type = JsonAnnotations.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(fieldDescriptions.size(), is(3));
        // @JsonPropertyOrder puts it to first place
        assertThat(fieldDescriptions.get(0),
                is(descriptor("uri", "String", "A uri", true)));
        // @JsonProperty
        assertThat(fieldDescriptions.get(1),
                is(descriptor("path", "String", "A location", true)));
        // @JsonGetter
        assertThat(fieldDescriptions.get(2),
                is(descriptor("param", "String", "A parameter", true)));
    }

    @Test
    public void testGenerateDocumentationForFieldResolution() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY));

        JavadocReader javadocReader = mock(JavadocReader.class);
        when(javadocReader.resolveFieldComment(FieldCommentResolution.class, "location"))
                .thenReturn("A location");
        when(javadocReader.resolveMethodComment(FieldCommentResolution.class, "getType"))
                .thenReturn("A type");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), javadocReader);
        Type type = FieldCommentResolution.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(fieldDescriptions.size(), is(2));
        // field comment
        assertThat(fieldDescriptions.get(0),
                is(descriptor("location", "String", "A location", true)));
        // getter comment
        assertThat(fieldDescriptions.get(1),
                is(descriptor("type", "String", "A type", true)));
    }

    private ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        return mapper;
    }

    private ExtendedFieldDescriptor descriptor(String path, Object fieldType,
            String comment, boolean optional) {
        FieldDescriptor fieldDescriptor = fieldWithPath(path)
                .type(fieldType)
                .description(comment);
        if (optional) {
            fieldDescriptor.optional();
        }
        return new ExtendedFieldDescriptor(fieldDescriptor);
    }


    private List<ExtendedFieldDescriptor> cast(List<FieldDescriptor> original) {
        List<ExtendedFieldDescriptor> casted = new ArrayList<>(original.size());
        for (FieldDescriptor d : original) {
            casted.add(new ExtendedFieldDescriptor(d));
        }
        return casted;
    }

    private static class PrimitiveTypes {
        @NotBlank
        private String stringField;
        private Boolean booleanField;
        @NotNull
        private Integer numberField1;
        private Double numberField2;
    }

    private static class ComposedTypes {
        @NotNull
        private PrimitiveTypes objectField;
        private List<PrimitiveTypes> arrayField;
    }

    private static class FirstLevel {
        private SecondLevel second;
    }

    private static class SecondLevel {
        @NotEmpty
        private List<ThirdLevel> third;
    }

    private static class ThirdLevel {
        private FourthLevel fourth;
    }

    private static class FourthLevel {
        private List<FifthLevel> fifth;
    }

    private static class FifthLevel {
        private Integer last;
    }

    private static class ExternalSerializer {
        private BigDecimal bigDecimal;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL) // does not affect field resolution
    @JsonIgnoreProperties({"value"})
    @JsonPropertyOrder({"uri", "path"})
    private static class JsonAnnotations {
        @JsonProperty("path")
        private String location;

        @JsonIgnore
        private String type;

        private String value;

        private String uri;

        private String param;

        @JsonGetter("param")
        public String getParameter() {
            return param;
        }
    }

    private static class FieldCommentResolution {
        public String location;
        private String type;

        public String getType() {
            return type;
        }
    }
}
