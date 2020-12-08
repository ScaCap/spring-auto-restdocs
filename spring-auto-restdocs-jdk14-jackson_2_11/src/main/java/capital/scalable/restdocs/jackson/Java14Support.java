/*-
 * #%L
 * Spring Auto REST Docs Java Record Support
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
package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.*;

import java.lang.reflect.RecordComponent;
import java.util.Map;

import static java.util.Arrays.asList;

abstract class Java14Support {

    static boolean hasExplicitName(Annotated annotated) {
        return true
            && annotated.hasAnnotation(JsonProperty.class)
            && !annotated.getAnnotation(JsonProperty.class).value().isBlank()
            ;
    }

    public static ClassIntrospector classIntrospector() {
        return new Java14ClassIntrospector();
    }

    public static AnnotationIntrospector recordIntrospector() {
        return new RecordIntrospector();
    }

    private static class Java14ClassIntrospector extends BasicClassIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public ClassIntrospector copy() {
            return new Java14ClassIntrospector();
        }

        @Override
        protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix) {
            return new PropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
        }

    }

    /**
     * Jackson can automatically detect properties that follow the JavaBeans
     * specification by looking for methods whose name starts with either
     * "get" or "is", returns a value and accepts no arguments. These methods
     * are known formally as accessors and informally as getters.
     * <p>
     * A Java 14 record does not follow the JavaBeans specification and its
     * accessor names do not follow the naming pattern, which requires
     * annotating every record property with @JsonProperty.
     * </p><p>
     * This class overrides the default behavior to recognize a record's
     * accessors so that annotations are no longer required.
     * </p>
     */
    private static class PropertiesCollector extends POJOPropertiesCollector {

        public PropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
            super(config, forSerialization, type, classDef, mutatorPrefix);
        }

        @Override
        protected void _addGetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
            super._addGetterMethod(props, m, isRecordAccessor(m) ? new RecordAccessorName(m, ai): ai);
        }

        @Override
        protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props) {
            // keep everything
        }

        private static boolean isRecordAccessor(AnnotatedMethod annotatedMethod) {
            var c = annotatedMethod.getDeclaringClass();
            var m = annotatedMethod.getMember();

            return true
                && !hasExplicitName(annotatedMethod)
                && c.isRecord()
                && asList(c.getRecordComponents())
                    .stream()
                    .map(RecordComponent::getAccessor)
                    .anyMatch(accessor -> accessor.equals(m))
                ;
        }

        private static final class RecordAccessorName extends DelegatingAnnotationIntrospector {
            private static final long serialVersionUID = 1L;

            private final String accessorName;

            private RecordAccessorName(AnnotatedMethod m, AnnotationIntrospector annotationIntrospector) {
                super(annotationIntrospector);
                this.accessorName = m.getName();
            }

            @Override
            public PropertyName findNameForSerialization(Annotated a) {
                return PropertyName.construct(this.accessorName);
            }

        }

    }

    /**
     * Jackson has two modes of operation when creating an object using its constructor:
     * property-based and delegate-based. By default, Jackson attempts to infer which mode
     * to use based on the number of constructor arguments needed.
     * <p>
     * Property-based creators are used when the constructor requires more than one argument,
     * the constructor requires no arguments (i.e., the properties are supplied using setters)
     * or the single argument is annotated with @JsonProperty.
     * </p><p>
     * Delegate-based creators take just one argument, which is NOT annotated with @JsonProperty.
     * Type of that property is used by Jackson to bind the whole JSON value (JSON Object, array
     * or scalar value), to be passed as value of that one argument.
     * </p><p>
     * This strategy works well for third-party classes like URI and UUID (i.e., a value-type)
     * that do not use Jackson annotations. However, this poses a problem when using Java 14
     * records since it will be a common occurrence to have a single constructor argument and
     * records will never represent a value-type.
     * </p><p>
     * There are several ways to address this issue:
     * </p>
     * <dl>
     * <dt>Configure ObjectMapper to always use property-based creators</dt>
     * <dd>
     * This poses a problem when the object graph actually has value-types like URI and UUID
     * since these properties can no longer be deserialized without providing ObjectMapper
     * with custom deserializers for each value-type used.
     * </dd>
     * <dt>Add a @JsonCreator annotation to the constructor specifying the mode to use</dt>
     * <dd>
     * This poses a problem for records because it requires the developer to specify the
     * constructor twice: once to supply the property definitions and one to supply the
     * annotation.
     * </dd>
     * <dt>Add a @JsonProperty annotation on the single constructor argument</dt>
     * <dd>
     * This poses a problem in that it requires the developer to understand and remember
     * Jackson's creator rules. It is not obvious when reading the code why some records
     * use @JsonProperty and other do not.
     * </dd>
     * <dt>Add a custom annotation introspector to override the default behavior</dt>
     * <dd>
     * This is the approach we adopt here. By providing Jackson with an explicit name
     * for the constructor's single argument, it behaves as if the argument is annotated
     * with @JsonProperty. We only apply this strategy when deserializing a record to
     * preserve the default behavior. We also check that the constructor argument does
     * not already have a @JsonProperty annotation in case the annotation supplies a
     * name that is different from the property's name.
     * </dd>
     * </dl>
     */
    private static class RecordIntrospector extends NopAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public PropertyName findNameForDeserialization(Annotated annotated) {
            if (true
                && annotated instanceof AnnotatedParameter annotatedParameter
                && annotatedParameter.getDeclaringClass().isRecord()
                && annotatedParameter.getOwner() instanceof AnnotatedConstructor annotatedConstructor
                && 1 == annotatedConstructor.getParameterCount()
                && !hasExplicitName(annotated)
            ) {
                // Jackson still supports Java 7 and the Parameter class first appears
                // in Java 8 so we have to ask the constructor for the parameter.
                var parameter = annotatedConstructor.getAnnotated().getParameters()[annotatedParameter.getIndex()];
                if (parameter.isNamePresent()) {
                    return PropertyName.construct(parameter.getName());
                }
            }

            return null;
        }

    }

}
