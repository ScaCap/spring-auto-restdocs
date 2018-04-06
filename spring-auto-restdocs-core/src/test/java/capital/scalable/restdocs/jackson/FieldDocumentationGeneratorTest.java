/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hibernate.validator.constraints.Length;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;

public class FieldDocumentationGeneratorTest {

    private JavadocReader javadocReader = mock(JavadocReader.class);
    private ConstraintReader constraintReader = mock(ConstraintReader.class);

    @Test
    public void testGenerateDocumentationForBasicTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(BasicTypes.class, "string", "A string");
        mockFieldComment(BasicTypes.class, "bool", "A boolean");
        mockFieldComment(BasicTypes.class, "number", "An integer");
        mockFieldComment(BasicTypes.class, "decimal", "A decimal");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), mapper.getDeserializationConfig(),
                        javadocReader, constraintReader);
        Type type = BasicTypes.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(result.size(), is(4));
        assertThat(result.get(0), is(descriptor("string", "String", "A string", "true")));
        assertThat(result.get(1), is(descriptor("bool", "Boolean", "A boolean", "true")));
        assertThat(result.get(2), is(descriptor("number", "Integer", "An integer", "true")));
        assertThat(result.get(3), is(descriptor("decimal", "Decimal", "A decimal", "true")));
    }

    @Test
    public void testGenerateDocumentationForPrimitiveTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(PrimitiveTypes.class, "string", "A string");
        mockFieldComment(PrimitiveTypes.class, "bool", "A boolean");
        mockFieldComment(PrimitiveTypes.class, "number", "An integer");
        mockFieldComment(PrimitiveTypes.class, "decimal", "A decimal");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = PrimitiveTypes.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(result.size(), is(4));
        assertThat(result.get(0), is(descriptor("string", "Array[String]", "A string", "true")));
        assertThat(result.get(1), is(descriptor("bool", "Boolean", "A boolean", "true")));
        assertThat(result.get(2), is(descriptor("number", "Integer", "An integer", "true")));
        assertThat(result.get(3), is(descriptor("decimal", "Decimal", "A decimal", "true")));

        // when change deserialization config
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        generator = new FieldDocumentationGenerator(mapper.writer(),
                mapper.getDeserializationConfig(), javadocReader, constraintReader);

        // when
        result = cast(generator.generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(4));
        assertThat(result.get(0), is(descriptor("string", "Array[String]", "A string", "true")));
        assertThat(result.get(1), is(descriptor("bool", "Boolean", "A boolean", "false")));
        assertThat(result.get(2), is(descriptor("number", "Integer", "An integer", "false")));
        assertThat(result.get(3), is(descriptor("decimal", "Decimal", "A decimal", "false")));
    }

    @Test
    public void testGenerateDocumentationForComposedTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(ComposedTypes.class, "object", "An object");
        mockFieldComment(BasicTypes.class, "string", "A string");
        mockFieldComment(BasicTypes.class, "bool", "A boolean");
        mockFieldComment(BasicTypes.class, "number", "An integer");
        mockFieldComment(BasicTypes.class, "decimal", "A decimal");
        mockFieldComment(ComposedTypes.class, "array", "An array");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = ComposedTypes.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(10));
        assertThat(result.get(0),
                is(descriptor("object", "Object", "An object", "true")));
        assertThat(result.get(1),
                is(descriptor("object.string", "String", "A string", "true")));
        assertThat(result.get(2),
                is(descriptor("object.bool", "Boolean", "A boolean", "true")));
        assertThat(result.get(3),
                is(descriptor("object.number", "Integer", "An integer", "true")));
        assertThat(result.get(4),
                is(descriptor("object.decimal", "Decimal", "A decimal", "true")));
        assertThat(result.get(5),
                is(descriptor("array", "Array[Object]", "An array", "true")));
        assertThat(result.get(6),
                is(descriptor("array[].string", "String", "A string", "true")));
        assertThat(result.get(7),
                is(descriptor("array[].bool", "Boolean", "A boolean", "true")));
        assertThat(result.get(8),
                is(descriptor("array[].number", "Integer", "An integer", "true")));
        assertThat(result.get(9),
                is(descriptor("array[].decimal", "Decimal", "A decimal", "true")));
    }

    @Test
    public void testGenerateDocumentationForNestedTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(FirstLevel.class, "second", "2nd level");
        mockFieldComment(SecondLevel.class, "third", "3rd level");
        mockFieldComment(ThirdLevel.class, "fourth", "4th level");
        mockFieldComment(FourthLevel.class, "fifth", "5th level");
        mockFieldComment(FifthLevel.class, "last", "An integer");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = FirstLevel.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(5));
        assertThat(result.get(0),
                is(descriptor("second", "Object", "2nd level", "true")));
        assertThat(result.get(1),
                is(descriptor("second.third", "Array[Object]", "3rd level", "true")));
        assertThat(result.get(2),
                is(descriptor("second.third[].fourth", "Object", "4th level", "true")));
        assertThat(result.get(3),
                is(descriptor("second.third[].fourth.fifth", "Array[Object]", "5th level",
                        "true")));
        assertThat(result.get(4),
                is(descriptor("second.third[].fourth.fifth[].last", "Integer", "An integer",
                        "true")));
    }

    @Test
    public void testGenerateDocumentationForRecursiveTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        FieldDocumentationGenerator generator = new FieldDocumentationGenerator(mapper.writer(),
                mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = RecursiveType.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(16));
        assertThat(result.get(0), is(descriptor("sub1", "Array[Object]", "", "true")));
        assertThat(result.get(1), is(descriptor("sub2", "Object", "", "true")));
        assertThat(result.get(2), is(descriptor("sub3", "Array[Object]", "", "true")));
        assertThat(result.get(3), is(descriptor("sub4", "Object", "", "true")));
        assertThat(result.get(4), is(descriptor("sub5", "Array[Object]", "", "true")));
        assertThat(result.get(5), is(descriptor("sub6", "Object", "", "true")));
        assertThat(result.get(6), is(descriptor("sub7", "Array[Object]", "", "true")));
        assertThat(result.get(7), is(descriptor("sub7[].sub1", "Array[Object]", "", "true")));
        assertThat(result.get(8), is(descriptor("sub7[].sub2", "Object", "", "true")));
        assertThat(result.get(9), is(descriptor("sub7[].sub3", "Array[Object]", "", "true")));
        assertThat(result.get(10), is(descriptor("sub7[].sub4", "Object", "", "true")));
        assertThat(result.get(11), is(descriptor("sub8", "Object", "", "true")));
        assertThat(result.get(12), is(descriptor("sub8.sub1", "Array[Object]", "", "true")));
        assertThat(result.get(13), is(descriptor("sub8.sub2", "Object", "", "true")));
        assertThat(result.get(14), is(descriptor("sub8.sub3", "Array[Object]", "", "true")));
        assertThat(result.get(15), is(descriptor("sub8.sub4", "Object", "", "true")));
    }

    @Test
    public void testGenerateDocumentationForExternalSerializer() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(ExternalSerializer.class, "bigDecimal", "A decimal");

        SimpleModule testModule = new SimpleModule("TestModule");
        testModule.addSerializer(new BigDecimalSerializer());
        mapper.registerModule(testModule);

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = ExternalSerializer.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(descriptor("bigDecimal", "Decimal", "A decimal", "true")));
    }

    @Test
    public void testGenerateDocumentationForJacksonAnnotations() throws Exception {
        // given
        ObjectMapper mapper = createMapper();

        mockFieldComment(JsonAnnotations.class, "location", "A location");
        mockFieldComment(JsonAnnotations.class, "uri", "A uri");
        when(javadocReader.resolveMethodComment(JsonAnnotations.class, "getParameter"))
                .thenReturn("A parameter");
        mockFieldComment(JsonAnnotations.Meta.class, "headers", "A header map");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = JsonAnnotations.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(4));
        // @JsonPropertyOrder puts it to first place
        assertThat(result.get(0), is(descriptor("uri", "String", "A uri", "true")));
        // @JsonProperty
        assertThat(result.get(1), is(descriptor("path", "String", "A location", "true")));
        // @JsonGetter
        assertThat(result.get(2), is(descriptor("param", "String", "A parameter", "true")));
        // @JsonUnwrapped
        assertThat(result.get(3), is(descriptor("headers", "Map", "A header map", "true")));
    }

    @Test
    public void testGenerateDocumentationForFieldResolution() throws Exception {
        // given
        // different mapper for custom field resolution
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY));

        // comment on field directly
        mockFieldComment(FieldCommentResolution.class, "location", "A location");
        // comment on getter instead of field
        when(javadocReader.resolveMethodComment(FieldCommentResolution.class, "getType"))
                .thenReturn("A type");
        // comment on field instead of getter
        mockFieldComment(FieldCommentResolution.class, "uri", "A uri");
        mockFieldComment(FieldCommentResolution.class, "secured", "A secured flag");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = FieldCommentResolution.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(result.size(), is(4));
        // field comment
        assertThat(result.get(0), is(descriptor("location", "String", "A location", "true")));
        // getter comment
        assertThat(result.get(1), is(descriptor("type", "String", "A type", "true")));
        // field comment
        assertThat(result.get(2), is(descriptor("uri", "String", "A uri", "true")));
        assertThat(result.get(3), is(descriptor("secured", "Boolean", "A secured flag", "true")));
    }

    @Test
    public void testGenerateDocumentationForConstraints() throws Exception {
        // given
        ObjectMapper mapper = createMapper();

        mockConstraint(ConstraintResolution.class, "location", "A constraint for location");
        mockConstraint(ConstraintResolution.class, "type", "A constraint for type");
        mockOptional(ConstraintResolution.class, "type", "false");
        mockOptional(ConstraintResolution.class, "params", "false");
        mockConstraint(ConstraintField.class, "value", "A constraint1 for value",
                "A constraint2 for value");
        mockOptional(ConstraintField.class, "value", "false");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = ConstraintResolution.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(fieldDescriptions.size(), is(5));
        assertThat(fieldDescriptions.get(0),
                is(descriptor("location", "String", "", "true", "A constraint for location")));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("type", "Integer", "", "false", "A constraint for type")));
        assertThat(fieldDescriptions.get(2),
                is(descriptor("params", "Array[Object]", "", "false")));
        assertThat(fieldDescriptions.get(3),
                is(descriptor("params[].value", "String", "", "false",
                        "A constraint1 for value", "A constraint2 for value")));
        assertThat(fieldDescriptions.get(4),
                is(descriptor("flags", "Array[Boolean]", "", "true")));
    }

    @Test
    public void testGenerateDocumentationForJacksonSubTypes() throws Exception {
        // given
        ObjectMapper mapper = createMapper();

        mockFieldComment(JsonType1.class, "name", "A name");
        mockFieldComment(JsonType1.class, "type", "A type");
        mockFieldComment(JsonType1.class, "base1", "A base 1");
        mockFieldComment(JsonType1.class, "base2", "A base 2");
        mockFieldComment(JsonType1.class, "base3", "A base 3");
        mockFieldComment(JsonType1.class, "base4", "A base 4");
        mockFieldComment(JsonType1SubType1.class, "base1Sub1", "A base 1 sub 1");
        mockFieldComment(JsonType1SubType2.class, "base1Sub2", "A base 1 sub 2");
        mockFieldComment(JsonType2.class, "clazz", "A clazz");
        mockFieldComment(JsonType2SubType1.class, "base2Sub1", "A base 2 sub 1");
        mockFieldComment(JsonType2SubType2.class, "base2Sub2", "A base 2 sub 2");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(),
                        mapper.getDeserializationConfig(), javadocReader, constraintReader);
        Type type = JsonType1.class;

        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));

        // then
        assertThat(fieldDescriptions.size(), is(14));
        assertThat(fieldDescriptions.get(0),
                is(descriptor("type", "String", "A type", "true")));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("name", "String", "A name", "true")));
        assertThat(fieldDescriptions.get(2),
                is(descriptor("base1", "Array[Object]", "A base 1", "true")));
        assertThat(fieldDescriptions.get(3),
                is(descriptor("base2", "Object", "A base 2", "true")));
        assertThat(fieldDescriptions.get(4),
                is(descriptor("base3", "Array[Object]", "A base 3", "true")));
        assertThat(fieldDescriptions.get(5),
                is(descriptor("base3[].clazz", "String", "A clazz", "true")));
        assertThat(fieldDescriptions.get(6),
                is(descriptor("base3[].base2Sub1", "String", "A base 2 sub 1", "true")));
        assertThat(fieldDescriptions.get(7),
                is(descriptor("base3[].base2Sub2", "String", "A base 2 sub 2", "true")));
        assertThat(fieldDescriptions.get(8),
                is(descriptor("base4", "Object", "A base 4", "true")));
        assertThat(fieldDescriptions.get(9),
                is(descriptor("base4.clazz", "String", "A clazz", "true")));
        assertThat(fieldDescriptions.get(10),
                is(descriptor("base4.base2Sub1", "String", "A base 2 sub 1", "true")));
        assertThat(fieldDescriptions.get(11),
                is(descriptor("base4.base2Sub2", "String", "A base 2 sub 2", "true")));
        assertThat(fieldDescriptions.get(12),
                is(descriptor("base1Sub1", "String", "A base 1 sub 1", "true")));
        assertThat(fieldDescriptions.get(13),
                is(descriptor("base1Sub2", "String", "A base 1 sub 2", "true")));
    }

    @Test
    public void testGenerateDocumentationWithTags() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(BasicTypes.class, "string", "A string");
        mockFieldTag(BasicTypes.class, "string", "see", "this");
        mockFieldComment(BasicTypes.class, "bool", "A boolean");
        mockFieldTag(BasicTypes.class, "bool", "see", "<a href=\"xyz\">docs</a>");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), mapper.getDeserializationConfig(),
                        javadocReader, constraintReader);
        Type type = BasicTypes.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(result.size(), is(4));
        assertThat(result.get(0),
                is(descriptor("string", "String", "A string<br>See this.", "true")));
        assertThat(result.get(1),
                is(descriptor("bool", "Boolean", "A boolean<br>See <a href=\"xyz\">docs</a>.",
                        "true")));
    }

    @Test
    public void testGenerateDocumentationForRequiredProperties() throws Exception {
        // given
        ObjectMapper mapper = createMapper();
        mockFieldComment(RequiredProperties.class, "string", "A string");
        mockFieldComment(RequiredProperties.class, "number", "An integer");

        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer(), mapper.getDeserializationConfig(),
                        javadocReader, constraintReader);
        Type type = RequiredProperties.class;

        // when
        List<ExtendedFieldDescriptor> result = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(descriptor("string", "String", "A string", "false")));
        assertThat(result.get(1), is(descriptor("number", "Integer", "An integer", "false")));
    }

    private OngoingStubbing<List<String>> mockOptional(Class<?> javaBaseClass, String fieldName,
            String value) {
        return when(constraintReader.getOptionalMessages(javaBaseClass, fieldName))
                .thenReturn(singletonList(value));
    }

    private void mockConstraint(Class<?> javaBaseClass, String fieldName, String... values) {
        when(constraintReader.getConstraintMessages(javaBaseClass, fieldName))
                .thenReturn(asList(values));
    }

    private void mockFieldComment(Class<?> javaBaseClass, String fieldName, String value) {
        when(javadocReader.resolveFieldComment(javaBaseClass, fieldName))
                .thenReturn(value);
    }

    private void mockFieldTag(Class<?> javaBaseClass, String fieldName, String tagName,
            String value) {
        when(javadocReader.resolveFieldTag(javaBaseClass, fieldName, tagName))
                .thenReturn(value);
    }

    private ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        return mapper;
    }

    private ExtendedFieldDescriptor descriptor(String path, Object fieldType,
            String comment, String optional, String... constraints) {
        FieldDescriptor fieldDescriptor = fieldWithPath(path)
                .type(fieldType)
                .description(comment);
        fieldDescriptor.attributes(
                new Attribute(OPTIONAL_ATTRIBUTE, singletonList(optional)));
        if (constraints != null) {
            fieldDescriptor.attributes(
                    new Attribute(CONSTRAINTS_ATTRIBUTE, asList(constraints)));
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

    private static class BasicTypes {
        private String string;
        private Boolean bool;
        private Integer number;
        private Double decimal;

    }

    private static class PrimitiveTypes {
        private char[] string; // array is nullable
        private boolean bool;
        private int number;
        private double decimal;
    }

    private static class ComposedTypes {
        private BasicTypes object;
        private List<BasicTypes> array;
    }

    private static class FirstLevel {
        private SecondLevel second;
    }

    private static class SecondLevel {
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

        @JsonUnwrapped
        private Meta meta;

        @JsonGetter("param")
        public String getParameter() {
            return param;
        }

        private class Meta {
            private Map<String, String> headers;
        }
    }

    private static class FieldCommentResolution {
        public String location; // doc is here
        public String type;
        private String uri; // doc is here
        private Boolean secured;// doc is here

        public String getType() { // doc is here
            return type;
        }

        public String getUri() {
            return uri;
        }

        public Boolean isSecured() {
            return secured;
        }
    }

    private static class ConstraintResolution {
        @Length(min = 1, max = 255)
        private String location;
        @NotNull
        @Min(1)
        @Max(10)
        private Integer type;
        @Valid
        @NotEmpty
        private List<ConstraintField> params;
        private List<Boolean> flags;
    }

    private static class ConstraintField {
        @NotBlank
        @Size(max = 20)
        private String value;
    }

    private static class RecursiveType {
        // explicitly prevented expansion
        @RestdocsNotExpanded
        private List<RecursiveType> sub1;
        @RestdocsNotExpanded
        private RecursiveType sub2;
        @RestdocsNotExpanded
        private List<RecursiveType2> sub3;
        @RestdocsNotExpanded
        private RecursiveType2 sub4;

        // implicitly prevented recursion
        private List<RecursiveType> sub5;
        private RecursiveType sub6;
        // unexplored type
        private List<RecursiveType2> sub7;
        private RecursiveType2 sub8;
    }

    private static class RecursiveType2 {
        // implicitly prevented recursion
        private List<RecursiveType> sub1;
        private RecursiveType sub2;
        private List<RecursiveType2> sub3;
        private RecursiveType2 sub4;
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = JsonType1SubType1.class, name = "SUB1"),
            @JsonSubTypes.Type(value = JsonType1SubType2.class, name = "SUB2"),
    })
    private static abstract class JsonType1 {
        private String type;
        private String name;
        private List<JsonType1> base1;
        private JsonType1 base2;
        private List<JsonType2> base3;
        private JsonType2 base4;
    }

    private static class JsonType1SubType1 extends JsonType1 {
        private String base1Sub1;
    }

    private static class JsonType1SubType2 extends JsonType1 {
        private String base1Sub2;
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "clazz",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = JsonType2SubType1.class, name = "x"),
            @JsonSubTypes.Type(value = JsonType2SubType2.class, name = "y"),
    })
    private static abstract class JsonType2 {
        private String clazz;
    }

    private static class JsonType2SubType1 extends JsonType2 {
        private String base2Sub1;
    }

    private static class JsonType2SubType2 extends JsonType2 {
        private String base2Sub2;
    }

    private static class RequiredProperties {
        @JsonProperty(required = true)
        private String string;
        @JsonProperty(required = true)
        private Integer number;
    }
}
