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

    private Set<JavaType> visited;

    public TypeRegistry(JavaType baseType) {
        visited = new HashSet<>();
        visited.add(baseType);

    }

    public TypeRegistry(Collection<JavaType> visited) {
        this.visited = new HashSet<>();
        this.visited.addAll(visited);
    }

    public TypeRegistry withVisitedTypes(List<JavaType> javaTypes) {
        Set<JavaType> result = new HashSet<>(visited);
        result.addAll(javaTypes);
        return new TypeRegistry(result);
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
}
