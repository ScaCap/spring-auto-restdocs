package capital.scalable.restdocs.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
