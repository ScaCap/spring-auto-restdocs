package capital.scalable.restdocs.util;

import static capital.scalable.restdocs.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.util.FieldUtil.isGetter;
import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

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

    public static boolean isPrimitive(Class<?> javaBaseClass, String javaFieldName) {
        Field field = ReflectionUtils.findField(javaBaseClass, javaFieldName);
        if (field == null && isGetter(javaFieldName)) {
            field = ReflectionUtils.findField(javaBaseClass, fromGetter(javaFieldName));
        }
        return field != null ? field.getType().isPrimitive() : false;
    }
}
