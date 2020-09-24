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
package capital.scalable.restdocs.constraints;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import capital.scalable.restdocs.i18n.SnippetTranslationManager;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;

public class ConstraintReaderImplTest {

    @Test
    public void getConstraintMessages() {
        ConstraintReader reader = createWithValidation();

        List<String> messages = reader.getConstraintMessages(Constraintz.class, "name");
        assertThat(messages).hasSize(0);

        messages = reader.getConstraintMessages(Constraintz.class, "index");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be at least 1");

        messages = reader.getConstraintMessages(Constraintz.class, "items");
        assertThat(messages).hasSize(0);

        messages = reader.getConstraintMessages(Constraintz.class, "amount");
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Must be at least 10");
        assertThat(messages.get(1)).isEqualTo("Must be at most 1000");

        messages = reader.getConstraintMessages(Constraintz.class, "type");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [big, small]");

        messages = reader.getConstraintMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Must be at least 10 (update)");
        assertThat(messages.get(1)).isEqualTo("Must be at most 1000 (update), Must be at most 1000 (create)");

        messages = reader.getConstraintMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be null (update)");

        messages = reader.getConstraintMessages(Constraintz.class, "num");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be at most 10 (groups: [UnresolvedGroup])");

        messages = reader.getConstraintMessages(Constraintz.class, "bool");
        assertThat(messages).hasSize(0);

        messages = reader.getConstraintMessages(Constraintz.class, "str");
        assertThat(messages).hasSize(0); // array

        messages = reader.getConstraintMessages(Constraintz.class, "enum1");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [ONE, TWO]");

        messages = reader.getConstraintMessages(Constraintz.class, "enum2");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Custom enum description: [A, B]");

        messages = reader.getConstraintMessages(Constraintz.class, "enum3");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [A first, B second]");

        messages = reader.getConstraintMessages(Constraintz.class, "optionalEnum");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [ONE, TWO]");

        messages = reader.getConstraintMessages(Constraintz.class, "sizedString");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be in range [2, 10]");
    }

    @Test
    public void getOptionalMessages() {
        ConstraintReader reader = createWithValidation();

        List<String> messages = reader.getOptionalMessages(Constraintz.class, "name");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("false");

        messages = reader.getOptionalMessages(Constraintz.class, "index");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("false");

        messages = reader.getOptionalMessages(Constraintz.class, "items");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("false");

        messages = reader.getOptionalMessages(Constraintz.class, "amount");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "type");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("false (create)");

        messages = reader.getOptionalMessages(Constraintz.class, "num");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "bool");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "str");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "enum1");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "enum2");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("false");

        messages = reader.getOptionalMessages(Constraintz.class, "optionalEnum");
        assertThat(messages).hasSize(0);

        messages = reader.getOptionalMessages(Constraintz.class, "sizedString");
        assertThat(messages).hasSize(0);
    }

    @Test
    public void getParameterConstraintMessages() throws NoSuchMethodException {
        ConstraintReader reader = createWithValidation();

        Method method = MethodTest.class.getMethod("exec", Integer.class, String.class,
                Enum1.class, Optional.class, BigDecimal.class);

        List<String> messages = reader.getConstraintMessages(new MethodParameter(method, 0));
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Must be at least 1 (create)");
        assertThat(messages.get(1)).isEqualTo("Must be at most 2 (update)");

        messages = reader.getConstraintMessages(new MethodParameter(method, 1));
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [all, single]");

        messages = reader.getConstraintMessages(new MethodParameter(method, 2));
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [ONE, TWO]");

        messages = reader.getConstraintMessages(new MethodParameter(method, 3));
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must be one of [ONE, TWO]");

        messages = reader.getConstraintMessages(new MethodParameter(method, 4));
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Must exceed 0.1");
    }

    @Test
    public void getConstraintMessages_validationNotPresent() {
        ConstraintReaderImpl reader = createWithoutValidation();
        assertThat(reader.getConstraintMessages(Constraintz.class, "index")).hasSize(0);
    }

    @Test
    public void getOptionalMessages_validationNotPresent() {
        ConstraintReaderImpl reader = createWithoutValidation();
        assertThat(reader.getOptionalMessages(Constraintz.class, "name")).hasSize(0);
    }

    @Test
    public void getParameterConstraintMessages_validationNotPresent() throws NoSuchMethodException {
        ConstraintReaderImpl reader = createWithoutValidation();
        Method method = MethodTest.class.getMethod("exec", Integer.class, String.class,
                Enum1.class, Optional.class, BigDecimal.class);
        assertThat(reader.getConstraintMessages(new MethodParameter(method, 0))).hasSize(0);
    }

    @Test
    public void getTypeSpecifier_resolved() {
        ConstraintReaderImpl reader = createWithoutValidation();
        assertThat(reader.getTypeSpecifier(Plain.class)).isEqualTo("[plain type]");
    }

    @Test
    public void getTypeSpecifier_default() {
        ConstraintReaderImpl reader = createWithoutValidation();
        assertThat(reader.getTypeSpecifier(Constraintz.class)).isEqualTo("");
    }

    @Test
    public void customConstraintDescriptionResolver() {
        ConstraintReader reader = ConstraintReaderImpl.createWithValidation(new ObjectMapper(),
                SnippetTranslationManager.getDefaultResolver(),
                new CustomConstraintDescriptionResolver());

        List<String> messages = reader.getConstraintMessages(Constraintz.class, "index");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo("Minimum");

    }

    private ConstraintReaderImpl createWithValidation() {
        return ConstraintReaderImpl.createWithValidation(
                new ObjectMapper(),
                SnippetTranslationManager.getDefaultResolver(),
                new DynamicResourceBundleConstraintDescriptionResolver()
        );
    }

    private ConstraintReaderImpl createWithoutValidation() {
        return ConstraintReaderImpl.createWithoutValidation(
                new ObjectMapper(),
                SnippetTranslationManager.getDefaultResolver(),
                new DynamicResourceBundleConstraintDescriptionResolver()
        );
    }
    static class CustomConstraintDescriptionResolver implements ConstraintDescriptionResolver {

        private final Map<String, String> customDescription = new HashMap<>();

        public CustomConstraintDescriptionResolver() {
            // completely custom storage and key format
            customDescription.put("javax.validation.constraints.Min", "Minimum");
        }

        @Override
        public String resolveDescription(Constraint constraint) {
            return customDescription.getOrDefault(constraint.getName(), "");
        }
    }

    static class Constraintz {
        @NotBlank
        private String name;

        @NotNull
        @Min(1)
        private Integer index;

        @Valid
        @NotEmpty
        private Collection<Boolean> items;

        @DecimalMin("10")
        @DecimalMax("1000")
        private BigDecimal amount;

        @OneOf({"big", "small"})
        private String type;

        @DecimalMin(value = "10", groups = Update.class)
        @DecimalMax(value = "1000", groups = {Update.class, Create.class})
        private BigDecimal amountWithGroup;

        @Null(groups = Update.class)
        @NotNull(groups = Create.class)
        private Integer indexWithGroup;

        @Max(value = 10, groups = UnresolvedGroup.class)
        private long num;

        private boolean bool;

        private char[] str;

        private Enum1 enum1;

        @NotNull
        private Enum2 enum2;

        private Enum3 enum3;

        private Optional<Enum1> optionalEnum;

        @Size(min = 2, max = 10, message = "Must be in range [${min}, ${max}]")
        private String sizedString;
    }

    enum Enum1 {ONE, TWO}

    enum Enum2 {A, B}

    enum Enum3 {
        A("A first"),
        B("B second");

        private String jsonValue;

        Enum3(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        @JsonValue
        public String getJsonValue() {
            return jsonValue;
        }
    }

    interface UnresolvedGroup {
    }

    static class MethodTest {
        public void exec(
                @NotNull
                @Min(value = 1, groups = Create.class)
                @Max(value = 2, groups = Update.class) Integer count,
                @NotBlank
                @OneOf({"all", "single"}) String type,
                Enum1 enumeration,
                Optional<Enum1> optionalEnum,
                @DecimalMin(value = "0.1", message = "Must exceed ${value}") BigDecimal min) {
        }
    }

    static class Plain {
    }
}
