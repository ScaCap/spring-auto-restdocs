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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.ServiceLoader.load;

public abstract class ObjectMapperAdapterFactory {

    public abstract ObjectMapper adapt(ObjectMapper objectMapper);

    public static ObjectMapperAdapterFactory instance() {
        return Self.INSTANCE;
    }

    private static class Self {
        private static final ObjectMapperAdapterFactory INSTANCE = initialize();

        private static ObjectMapperAdapterFactory initialize() {
            final Iterator<ObjectMapperAdapterFactory> it = load(ObjectMapperAdapterFactory.class, ObjectMapperAdapterFactory.class.getClassLoader()).iterator();
            if (!it.hasNext()) throw new NoSuchElementException("No value present");
            return it.next();
        }
    }

}
