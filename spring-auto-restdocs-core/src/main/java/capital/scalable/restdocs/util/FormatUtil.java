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
        return isNotBlank(text) && !text.endsWith(".") && !text.endsWith(" ")
                ? text + "."
                : text;
    }

    public static String join(String delimiter, Object... texts) {
        StringBuilder result = new StringBuilder();
        for (Object text : texts) {
            if (text != null && isNotBlank(text.toString())) {
                result.append(text.toString());
                result.append(delimiter);
            }
        }
        if (result.length() > 0) {
            return result.substring(0, result.length() - delimiter.length());
        } else {
            return result.toString();
        }
    }
}
