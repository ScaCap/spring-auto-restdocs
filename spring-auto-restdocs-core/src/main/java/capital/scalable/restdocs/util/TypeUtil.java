package capital.scalable.restdocs.util;

import static capital.scalable.restdocs.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.util.FieldUtil.isGetter;
import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;
import static org.apache.commons.lang3.StringUtils.chop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.util.ReflectionUtils;

public class TypeUtil {
    private TypeUtil() {
        // util
    }

    public static String determineTypeName(Class<?> type) {
        // should return the same as FieldDocumentationVisitorWrapper
        boolean isArray = false;
        String canonicalName = primitiveToWrapper(type).getCanonicalName();
        if (canonicalName.endsWith("[]")) {
            isArray = true;
            canonicalName = chop(chop(canonicalName));
        }

        String finalName = getSimpleName(canonicalName);
        if (isArray) {
            return "Array[" + finalName + "]";
        } else {
            return finalName;
        }
    }

    public static String determineArrayOfType(JavaType contentType) {
        if (contentType != null) {
            return "Array[" + determineTypeName(contentType.getRawClass()) + "]";
        } else {
            return "Array[Object]";
        }
    }

    private static String getSimpleName(String canonicalName) {
        switch (canonicalName) {
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
        case "java.lang.String":
            return "String";
        case "java.lang.Boolean":
            return "Boolean";
        default:
            return getSpecialTypeName(canonicalName);
        }
    }

    private static String getSpecialTypeName(String canonicalName) {
        switch (canonicalName) {
        case "java.util.Locale":
            return "String";
        default:
            return "Object";
        }
    }

    public static boolean isPrimitive(Class<?> javaBaseClass, String javaFieldName) {
        Field field = ReflectionUtils.findField(javaBaseClass, javaFieldName);
        if (field == null && isGetter(javaFieldName)) {
            field = ReflectionUtils.findField(javaBaseClass, fromGetter(javaFieldName));
        }
        return field != null && field.getType().isPrimitive();
    }

    public static List<JavaType> resolveAllTypes(JavaType javaType, TypeFactory typeFactory) {
        if (javaType.isCollectionLikeType() || javaType.isArrayType()) {
            return resolveArrayTypes(javaType.getContentType(), typeFactory);
        } else {
            return resolveNonArrayTypes(javaType, typeFactory);
        }
    }

    private static List<JavaType> resolveArrayTypes(JavaType contentType, TypeFactory typeFactory) {
        List<JavaType> contentTypes = resolveAllTypes(contentType, typeFactory);
        List<JavaType> result = new ArrayList<>();
        for (JavaType contentSubType : contentTypes) {
            JavaType contentSubTypeArray = typeFactory.constructArrayType(contentSubType);
            result.add(contentSubTypeArray);
        }
        return result;
    }

    private static List<JavaType> resolveNonArrayTypes(JavaType javaType, TypeFactory typeFactory) {
        List<JavaType> types = new ArrayList<>();
        types.add(javaType);

        Class<?> rawClass = javaType.getRawClass();
        JsonSubTypes jsonSubTypes = rawClass.getAnnotation(JsonSubTypes.class);
        if (jsonSubTypes != null) {
            for (JsonSubTypes.Type subType : jsonSubTypes.value()) {
                JavaType javaSubType = typeFactory.constructType(subType.value());
                types.add(javaSubType);
            }
        }

        return types;
    }
}
