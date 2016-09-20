package capital.scalable.restdocs.util;

import static org.springframework.util.StringUtils.arrayToDelimitedString;

public class ObjectUtil {
    private ObjectUtil() {
        // util
    }

    public static String arrayToString(Object[] o) {
        return "[" + arrayToDelimitedString(o, ", ") + "]";
    }
}
