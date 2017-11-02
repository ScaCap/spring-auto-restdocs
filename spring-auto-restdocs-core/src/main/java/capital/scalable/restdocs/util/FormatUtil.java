package capital.scalable.restdocs.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.StringUtils.arrayToDelimitedString;

public class FormatUtil {
    private FormatUtil() {
        // util
    }

    public static String arrayToString(Object[] o) {
        return "[" + arrayToDelimitedString(o, ", ") + "]";
    }

    public static String addDot(String text) {
        return isNotBlank(text) && !text.endsWith(".") ? text + "." : text;
    }
}
