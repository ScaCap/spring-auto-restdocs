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
package capital.scalable.restdocs.jackson;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;

/**
 * Overrides class inspector so that we keep properties othewise filtered out via {@link JsonProperty#access()} in
 * {@link POJOPropertiesCollector#_removeUnwantedAccessor(java.util.Map)}.
 * The actual filtering happens now in
 * {@link FieldDocumentationObjectVisitor#skipProperty(com.fasterxml.jackson.databind.BeanProperty)}
 */
public class SardObjectMapper extends ObjectMapper {
    public SardObjectMapper(ObjectMapper objectMapper) {
        super(objectMapper);
        this.setConfig(getSerializationConfig().with(new SardClassIntrospector()));
    }

    public static class SardClassIntrospector extends BasicClassIntrospector {
        @Override
        protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac,
                JavaType type, boolean forSerialization, String mutatorPrefix) {
            return new SardPOJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
        }
    }

    public static class SardPOJOPropertiesCollector extends POJOPropertiesCollector {

        protected SardPOJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization,
                JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
            super(config, forSerialization, type, classDef, mutatorPrefix);
        }

        @Override
        protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props) {
            // keep everything
        }
    }
}
