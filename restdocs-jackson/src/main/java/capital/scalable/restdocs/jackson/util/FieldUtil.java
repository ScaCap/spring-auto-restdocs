package capital.scalable.restdocs.jackson.util;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

public class FieldUtil {
    private FieldUtil() {
        // utils
    }

    public static String fromGetter(String javaMethodName) {
        int cut = javaMethodName.startsWith("get") ? "get".length() : "is".length();
        return uncapitalize(javaMethodName.substring(cut, javaMethodName.length()));
    }

    public static boolean isGetter(String javaFieldName) {
        return javaFieldName.startsWith("get") || javaFieldName.startsWith("is");
    }
}
