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
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class FieldDocumentationGeneratorTest {

    @Test
    public void testGenerateDocumentationForPrimitiveTypes() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer());
        Type type = PrimitiveTypes.class;
        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(fieldDescriptions.get(0), is(descriptor("stringField", STRING, "", false)));
        assertThat(fieldDescriptions.get(1), is(descriptor("booleanField", BOOLEAN, "", true)));
        assertThat(fieldDescriptions.get(2), is(descriptor("numberField1", NUMBER, "", false)));
        assertThat(fieldDescriptions.get(3), is(descriptor("numberField2", NUMBER, "", true)));
    }

    @Test
    public void testGenerateDocumentationForComposedTypes() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer());
        Type type = ComposedTypes.class;
        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(fieldDescriptions.get(0), is(descriptor("objectField", OBJECT, "", false)));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("objectField.stringField", STRING, "", false)));
        assertThat(fieldDescriptions.get(5), is(descriptor("arrayField", ARRAY, "", true)));
        assertThat(fieldDescriptions.get(6),
                is(descriptor("arrayField[].stringField", STRING, "", false)));
    }

    @Test
    public void testGenerateDocumentationForNestedTypes() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer());
        Type type = FirstLevel.class;
        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(fieldDescriptions.get(0), is(descriptor("second", OBJECT, "", true)));
        assertThat(fieldDescriptions.get(1), is(descriptor("second.third", ARRAY, "", false)));
        assertThat(fieldDescriptions.get(2),
                is(descriptor("second.third[].fourth", OBJECT, "", true)));
        assertThat(fieldDescriptions.get(3),
                is(descriptor("second.third[].fourth.fifth", ARRAY, "", true)));
        assertThat(fieldDescriptions.get(4),
                is(descriptor("second.third[].fourth.fifth[].last", NUMBER, "", true)));
    }

    @Test
    public void testGenerateDocumentationForExternalSerializer() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("TestModule");
        testModule.addSerializer(new BigDecimalSerializer());
        mapper.registerModule(testModule);
        FieldDocumentationGenerator generator =
                new FieldDocumentationGenerator(mapper.writer());
        Type type = ExternalSerializer.class;
        // when
        List<ExtendedFieldDescriptor> fieldDescriptions = cast(generator
                .generateDocumentation(type, mapper.getTypeFactory()));
        // then
        assertThat(fieldDescriptions.get(0), is(descriptor("bigDecimal", NUMBER, "", true)));
    }


    private ExtendedFieldDescriptor descriptor(String path, JsonFieldType jsonFieldType,
            String comment,
            boolean optional) {
        FieldDescriptor fieldDescriptor = fieldWithPath(path)
                .type(jsonFieldType)
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

        public String getStringField() {
            return stringField;
        }

        public Boolean getBooleanField() {
            return booleanField;
        }

        public Integer getNumberField1() {
            return numberField1;
        }

        public Double getNumberField2() {
            return numberField2;
        }
    }

    private static class ComposedTypes {
        @NotNull
        private PrimitiveTypes objectField;
        private List<PrimitiveTypes> arrayField;

        public PrimitiveTypes getObjectField() {
            return objectField;
        }

        public List<PrimitiveTypes> getArrayField() {
            return arrayField;
        }
    }

    private static class FirstLevel {
        private SecondLevel second;

        public SecondLevel getSecond() {
            return second;
        }
    }

    private static class SecondLevel {
        @NotEmpty
        private List<ThirdLevel> third;

        public List<ThirdLevel> getThird() {
            return third;
        }
    }

    private static class ThirdLevel {
        private FourthLevel fourth;

        public FourthLevel getFourth() {
            return fourth;
        }
    }

    private static class FourthLevel {
        private List<FifthLevel> fifth;

        public List<FifthLevel> getFifth() {
            return fifth;
        }
    }

    private static class FifthLevel {
        private Integer last;

        public Integer getLast() {
            return last;
        }
    }

    private static class ExternalSerializer {
        private BigDecimal bigDecimal;

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }
    }
}
