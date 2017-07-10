package capital.scalable.restdocs.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.SnippetException;

public class FieldDescriptorUtil {
    private FieldDescriptorUtil() {
        // util
    }

    public static void assertAllDocumented(Collection<FieldDescriptor> fieldDescriptors,
            String what) {
        List<String> undocumentedFields = new ArrayList<>();
        for (FieldDescriptor descriptor : fieldDescriptors) {
            if (isBlank((String) descriptor.getDescription())) {
                undocumentedFields.add(descriptor.getPath());
            }
        }
        if (!undocumentedFields.isEmpty()) {
            throw new SnippetException(
                    "Following " + what + " were not documented: " + undocumentedFields);
        }
    }
}
