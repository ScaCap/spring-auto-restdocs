/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;

class TypeRegistry {
    private static final Logger log = getLogger(TypeRegistry.class);

    private final Set<JavaType> visited;
    private final TypeMapping mapping;

    public TypeRegistry(TypeMapping mapping, JavaType baseType) {
        this.mapping = mapping;
        visited = new HashSet<>();
        visited.add(baseType);

    }

    public TypeRegistry(TypeMapping mapping, Collection<JavaType> visited) {
        this.mapping = mapping;
        this.visited = new HashSet<>();
        this.visited.addAll(visited);
    }

    public TypeRegistry withVisitedTypes(List<JavaType> javaTypes) {
        Set<JavaType> result = new HashSet<>(visited);
        result.addAll(javaTypes);
        return new TypeRegistry(mapping, result);
    }

    public boolean wasVisited(JavaType type) {
        log.trace(" - WAS VISITED? {}", type.getRawClass().getSimpleName());
        for (JavaType t : visited) {
            if (t.getRawClass().equals(type.getRawClass())) {
                log.trace("   - YES {}", t.getRawClass().getSimpleName());
                return true;
            }
            log.trace("   - NO {}", t.getRawClass().getSimpleName());
        }
        return false;
    }

    public TypeMapping getTypeMapping() {
        return mapping;
    }
}
