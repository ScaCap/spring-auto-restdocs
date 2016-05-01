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
        assertThat(fieldDescriptions.get(0), is(descriptor("stringField", "String", "", false)));
        assertThat(fieldDescriptions.get(1), is(descriptor("booleanField", "Boolean", "", true)));
        assertThat(fieldDescriptions.get(2), is(descriptor("numberField1", "Integer", "", false)));
        assertThat(fieldDescriptions.get(3), is(descriptor("numberField2", "Decimal", "", true)));
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
        assertThat(fieldDescriptions.size(), is(6));
        assertThat(fieldDescriptions.get(0), is(descriptor("objectField", "Object", "", false)));
        assertThat(fieldDescriptions.get(1),
                is(descriptor("objectField.stringField", "String", "", false)));
        assertThat(fieldDescriptions.get(5), is(descriptor("arrayField", "Array", "", true)));
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
        assertThat(fieldDescriptions.get(0), is(descriptor("second", "Object", "", true)));
        assertThat(fieldDescriptions.get(1), is(descriptor("second.third", "Array", "", false)));
        assertThat(fieldDescriptions.get(2),
                is(descriptor("second.third[].fourth", "Object", "", true)));
        assertThat(fieldDescriptions.get(3),
                is(descriptor("second.third[].fourth.fifth", "Array", "", true)));
        assertThat(fieldDescriptions.get(4),
                is(descriptor("second.third[].fourth.fifth[].last", "Integer", "", true)));
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
        assertThat(fieldDescriptions.get(0), is(descriptor("bigDecimal", "Decimal", "", true)));
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
