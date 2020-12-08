/*-
 * #%L
 * Spring Auto REST Docs Java 8 Support Using Jackson 2.12 and Above
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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;

/**
 * Overrides class inspector so that we keep properties otherwise filtered out via {@link JsonProperty#access()} in
 * {@link POJOPropertiesCollector#_removeUnwantedAccessor(java.util.Map)}.
 * The actual filtering happens now in
 * {@link FieldDocumentationObjectVisitor#skipProperty(com.fasterxml.jackson.databind.BeanProperty)}
 */
class Jdk8_Jackson_2_12_ObjectMapperAdapter extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public Jdk8_Jackson_2_12_ObjectMapperAdapter(ObjectMapper objectMapper) {
        super(objectMapper);
        this.setConfig(getSerializationConfig().with(new SardClassIntrospector()));
    }

    public static class SardClassIntrospector extends BasicClassIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass classDef,
            JavaType type, boolean forSerialization, AccessorNamingStrategy accNaming) {
            return new SardPOJOPropertiesCollector(config, forSerialization, type, classDef, accNaming);
        }
    }

    public static class SardPOJOPropertiesCollector extends POJOPropertiesCollector {

        protected SardPOJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization,
                JavaType type, AnnotatedClass classDef, AccessorNamingStrategy accNaming) {
            super(config, forSerialization, type, classDef, accNaming);
        }

        @Override
        protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props) {
            // keep everything
        }
    }
}
