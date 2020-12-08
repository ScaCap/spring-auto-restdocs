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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;

/**
 * Overrides class inspector so that we keep properties otherwise filtered out via {@link JsonProperty#access()} in
 * {@link POJOPropertiesCollector#_removeUnwantedAccessor(java.util.Map)}.
 * The actual filtering happens now in
 * {@link FieldDocumentationObjectVisitor#skipProperty(com.fasterxml.jackson.databind.BeanProperty)}
 */
class Jdk14_Jackson_2_11_ObjectMapperAdapter extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public Jdk14_Jackson_2_11_ObjectMapperAdapter(ObjectMapper objectMapper) {
        super(objectMapper);
        this.setConfig(getSerializationConfig().with(Java14Support.classIntrospector()));
    }

}
