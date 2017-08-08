package capital.scalable.restdocs.util;

import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;

public class TypeUtil {
    private TypeUtil() {
        // util
    }

    public static String determineTypeName(Class<?> type) {
        // should return the same as FieldDocumentationVisitorWrapper
        switch (primitiveToWrapper(type).getCanonicalName()) {
        case "java.lang.Byte":
        case "java.lang.Short":
        case "java.lang.Long":
        case "java.lang.Integer":
        case "java.math.BigInteger":
            return "Integer";
        case "java.lang.Float":
        case "java.lang.Double":
        case "java.math.BigDecimal":
            return "Decimal";
        case "java.lang.Character":
            return "String";
        case "java.lang.Boolean":
            return "Boolean";
        default:
            return type.getSimpleName();
        }
    }


}
