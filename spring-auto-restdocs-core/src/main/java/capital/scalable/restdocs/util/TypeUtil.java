/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package capital.scalable.restdocs.util;

import static capital.scalable.restdocs.util.FieldUtil.fromGetter;
import static capital.scalable.restdocs.util.FieldUtil.isGetter;
import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;
import static org.apache.commons.lang3.StringUtils.chop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        if (type.isEnum()) {
            return "String";
        }
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
        Field field = findField(javaBaseClass, javaFieldName);
        return field != null && field.getType().isPrimitive();
    }

    public static Field findField(Class<?> javaBaseClass, String javaFieldOrMethodName) {
        Field field = ReflectionUtils.findField(javaBaseClass, javaFieldOrMethodName);
        if (field == null && isGetter(javaFieldOrMethodName)) {
            field = ReflectionUtils.findField(javaBaseClass, fromGetter(javaFieldOrMethodName));
        }
        return field;
    }

    public static Method findMethod(Class<?> javaBaseClass, String javaMethodName) {
        return ReflectionUtils.findMethod(javaBaseClass, javaMethodName);
    }

    public static <T extends Annotation> T resolveAnnotation(Class<?> javaBaseClass,
            String javaFieldOrMethodName, Class<T> annotationClass) {
        // prefer method lookup
        T annotation = findMethodAnnotation(javaBaseClass, javaFieldOrMethodName, annotationClass);
        if (annotation == null) {
            // fallback to field
            return findFieldAnnotation(javaBaseClass, javaFieldOrMethodName, annotationClass);
        }
        return annotation;
    }

    private static <T extends Annotation> T findMethodAnnotation(Class<?> javaBaseClass,
            String javaMethodName, Class<T> annotationClass) {
        Method method = findMethod(javaBaseClass, javaMethodName);
        return method != null ? method.getAnnotation(annotationClass) : null;
    }

    private static <T extends Annotation> T findFieldAnnotation(Class<?> javaBaseClass,
            String javaFieldOrMethodName, Class<T> annotationClass) {
        T annotation = null;
        // try field name
        Field field = ReflectionUtils.findField(javaBaseClass, javaFieldOrMethodName);
        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
        }
        // try field name from getter
        if (annotation == null && isGetter(javaFieldOrMethodName)) {
            field = ReflectionUtils.findField(javaBaseClass, fromGetter(javaFieldOrMethodName));
        }
        return field != null ? field.getAnnotation(annotationClass) : null;
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
