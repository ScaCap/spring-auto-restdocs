package capital.scalable.restdocs.jackson;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.restdocs.payload.FieldDescriptor;

public class FieldDescriptors {
    private Map<String, FieldDescriptor> map = new LinkedHashMap<>();
    private String noContentMessageKey;

    public void putIfAbsent(String path, FieldDescriptor descriptor) {
        map.putIfAbsent(path, descriptor);
    }

    public Collection<FieldDescriptor> values() {
        return map.values();
    }

    public String getNoContentMessageKey() {
        return noContentMessageKey;
    }

    void setNoContentMessageKey(String noContentMessageKey) {
        this.noContentMessageKey = noContentMessageKey;
    }
}
