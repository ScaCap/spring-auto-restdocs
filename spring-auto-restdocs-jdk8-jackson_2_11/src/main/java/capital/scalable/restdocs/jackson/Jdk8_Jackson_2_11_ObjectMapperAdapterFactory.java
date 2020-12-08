/*-
 * #%L
 * Spring Auto REST Docs Java 8 Support Using Jackson 2.11 and Below
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

import com.fasterxml.jackson.databind.ObjectMapper;

class Jdk8_Jackson_2_11_ObjectMapperAdapterFactory extends ObjectMapperAdapterFactory {

    @Override
    public ObjectMapper adapt(ObjectMapper objectMapper) {
        return new Jdk8_Jackson_2_11_ObjectMapperAdapter(objectMapper);
    }

}
