package capital.scalable.restdocs.util;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

public final class FieldUtil {
    private FieldUtil() {
        // utils
    }

    /**
     * Creates a field name from getter
     */
    public static String fromGetter(String javaMethodName) {
        if (!isGetter(javaMethodName)) {
            return javaMethodName;
        }

        int cut = javaMethodName.startsWith("get") ? "get".length() : "is".length();
        return uncapitalize(javaMethodName.substring(cut, javaMethodName.length()));
    }

    /**
     * Determines if method is getter
     */
    public static boolean isGetter(String javaMethodName) {
        return javaMethodName.startsWith("get") || javaMethodName.startsWith("is");
    }
}
