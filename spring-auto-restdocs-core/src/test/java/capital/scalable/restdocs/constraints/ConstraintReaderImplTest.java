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

import static capital.scalable.restdocs.constraints.ConstraintReaderImpl.createWithValidation;
import static capital.scalable.restdocs.constraints.ConstraintReaderImpl.createWithoutValidation;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

public class ConstraintReaderImplTest {

    @Test
    public void getConstraintMessages() {
        ConstraintReader reader = createWithValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());

        List<String> messages = reader.getConstraintMessages(Constraintz.class, "name");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "index");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be at least 1"));

        messages = reader.getConstraintMessages(Constraintz.class, "items");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "amount");
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("Must be at least 10"));
        assertThat(messages.get(1), is("Must be at most 1000"));

        messages = reader.getConstraintMessages(Constraintz.class, "type");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [big, small]"));

        messages = reader.getConstraintMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("Must be at least 10 (update)"));
        assertThat(messages.get(1),
                is("Must be at most 1000 (update), Must be at most 1000 (create)"));

        messages = reader.getConstraintMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be null (update)"));

        messages = reader.getConstraintMessages(Constraintz.class, "num");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be at most 10 (groups: [UnresolvedGroup])"));

        messages = reader.getConstraintMessages(Constraintz.class, "bool");
        assertThat(messages.size(), is(0));

        messages = reader.getConstraintMessages(Constraintz.class, "str");
        assertThat(messages.size(), is(0)); // array

        messages = reader.getConstraintMessages(Constraintz.class, "enum1");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [ONE, TWO]"));

        messages = reader.getConstraintMessages(Constraintz.class, "enum2");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Custom enum description: [A, B]"));

        messages = reader.getConstraintMessages(Constraintz.class, "enum3");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [A first, B second]"));

        messages = reader.getConstraintMessages(Constraintz.class, "optionalEnum");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [ONE, TWO]"));

        messages = reader.getConstraintMessages(Constraintz.class, "sizedString");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be of reasonable size"));
    }

    @Test
    public void getOptionalMessages() {
        ConstraintReader reader = createWithValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());

        List<String> messages = reader.getOptionalMessages(Constraintz.class, "name");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "index");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "items");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "amount");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "type");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "amountWithGroup");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "indexWithGroup");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false (create)"));

        messages = reader.getOptionalMessages(Constraintz.class, "num");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "bool");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "str");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "enum1");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "enum2");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("false"));

        messages = reader.getOptionalMessages(Constraintz.class, "optionalEnum");
        assertThat(messages.size(), is(0));

        messages = reader.getOptionalMessages(Constraintz.class, "sizedString");
        assertThat(messages.size(), is(0));
    }

    @Test
    public void getParameterConstraintMessages() throws NoSuchMethodException {
        ConstraintReader reader = createWithValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());

        Method method = MethodTest.class.getMethod("exec", Integer.class, String.class,
                Enum1.class, Optional.class, BigDecimal.class);

        List<String> messages = reader.getConstraintMessages(new MethodParameter(method, 0));
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0), is("Must be at least 1 (create)"));
        assertThat(messages.get(1), is("Must be at most 2 (update)"));

        messages = reader.getConstraintMessages(new MethodParameter(method, 1));
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [all, single]"));

        messages = reader.getConstraintMessages(new MethodParameter(method, 2));
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [ONE, TWO]"));

        messages = reader.getConstraintMessages(new MethodParameter(method, 3));
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must be one of [ONE, TWO]"));

        messages = reader.getConstraintMessages(new MethodParameter(method, 4));
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Must not be too small"));
    }

    @Test
    public void getConstraintMessages_validationNotPresent() {
        ConstraintReaderImpl reader = createWithoutValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
        assertThat(reader.getConstraintMessages(Constraintz.class, "index").size(), is(0));
    }

    @Test
    public void getOptionalMessages_validationNotPresent() {
        ConstraintReaderImpl reader = createWithoutValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
        assertThat(reader.getOptionalMessages(Constraintz.class, "name").size(), is(0));
    }

    @Test
    public void getParameterConstraintMessages_validationNotPresent() throws NoSuchMethodException {
        ConstraintReaderImpl reader = createWithoutValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
        Method method = MethodTest.class.getMethod("exec", Integer.class, String.class,
                Enum1.class, Optional.class, BigDecimal.class);
        assertThat(reader.getConstraintMessages(new MethodParameter(method, 0)).size(), is(0));
    }

    @Test
    public void getTypeSpecifier_resolved() {
        ConstraintReaderImpl reader = createWithoutValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
        assertThat(reader.getTypeSpecifier(Plain.class), is("[plain type]"));
    }

    @Test
    public void getTypeSpecifier_default() {
        ConstraintReaderImpl reader = createWithoutValidation(new ObjectMapper(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
        assertThat(reader.getTypeSpecifier(Constraintz.class), is(""));
    }

    @Test
    public void customConstraintDescriptionResolver() {

        ConstraintReader reader = createWithValidation(new ObjectMapper(),
                SnippetTranslationManager.getDefaultResolver(),
                new CustomConstraintDescriptionResolver());

        List<String> messages = reader.getConstraintMessages(Constraintz.class, "index");
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0), is("Custom must be at least 1"));

    }

    static class CustomConstraintDescriptionResolver implements ConstraintDescriptionResolver {
        private ResourceBundleConstraintDescriptionResolver delegate;
        private Map<String, String> customDescription;
        private PropertyPlaceholderHelper propertyPlaceholderHelper;

        public CustomConstraintDescriptionResolver() {
            delegate = new ResourceBundleConstraintDescriptionResolver();
            customDescription = new HashMap<>();
            propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

            customDescription.put("javax.validation.constraints.Min.description", "Custom must be at least ${value}");
        }

        @Override
        public String resolveDescription(Constraint constraint) {
            String key = constraint.getName() + ".description";
            if (customDescription.containsKey(key)) {
                return propertyPlaceholderHelper.replacePlaceholders(customDescription.get(key), new CustomConstraintDescriptionResolver.ConstraintPlaceholderResolver(constraint));
            }
            return delegate.resolveDescription(constraint);
        }

        private static final class ConstraintPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
            private final Constraint constraint;

            private ConstraintPlaceholderResolver(Constraint constraint) {
                this.constraint = constraint;
            }

            public String resolvePlaceholder(String placeholderName) {
                Object replacement = this.constraint.getConfiguration().get(placeholderName);
                if (replacement == null) {
                    return null;
                }
                if (replacement.getClass().isArray()) {
                    return StringUtils.arrayToDelimitedString((Object[]) replacement, ", ");
                }
                return replacement.toString();
            }
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

        @Size(min = 2, max = 10, message = "Must be of reasonable size")
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
                @DecimalMin(value = "0.1", message = "Must not be too small") BigDecimal min) {
        }
    }

    static class Plain {
    }
}
