/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.jackson.TypeMapping;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.CollectionFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

public class TypeUtil {
    private TypeUtil() {
        // util
    }

    public static String determineTypeName(Type unknownType) {
        Class<?> type = Object.class;
        Class<?> typeArgument = String.class;

        if (unknownType instanceof ParameterizedType) {
            type = (Class<?>) ((ParameterizedType) unknownType).getRawType();
            typeArgument = (Class<?>) ((ParameterizedType) unknownType).getActualTypeArguments()[0];
        } else if (unknownType instanceof Class<?>) {
            type = (Class<?>) unknownType;
        }

        boolean isArray = false;
        String canonicalName = primitiveToWrapper(type).getCanonicalName();
        if (canonicalName.endsWith("[]")) {
            isArray = true;
            canonicalName = chop(chop(canonicalName));
        } else if (CollectionFactory.isApproximableCollectionType(type)) {
            isArray = true;
            if (typeArgument != null) canonicalName = typeArgument.getTypeName();
        }

        if (isArray) {
            return "Array[" + getSimpleName(typeArgument, canonicalName) + "]";
        } else {
            return getSimpleName(type, canonicalName);
        }
    }

    public static String determineArrayOfType(JavaType contentType) {
        if (contentType != null) {
            return "Array[" + determineTypeName(contentType.getRawClass()) + "]";
        } else {
            return "Array[Object]";
        }
    }

    private static String getSimpleName(Class<?> type, String canonicalName) {
        // should return the same as FieldDocumentationVisitorWrapper
        if (type.isEnum()) {
            return "String";
        }

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

    public static List<JavaType> resolveAllTypes(JavaType javaType, TypeFactory typeFactory, TypeMapping typeMapping) {
        if (javaType.isCollectionLikeType() || javaType.isArrayType()) {
            return resolveArrayTypes(javaType.getContentType(), typeFactory, typeMapping);
        } else {
            return resolveNonArrayTypes(javaType, typeFactory, typeMapping);
        }
    }

    private static List<JavaType> resolveArrayTypes(JavaType contentType, TypeFactory typeFactory,
            TypeMapping typeMapping) {
        List<JavaType> contentTypes = resolveAllTypes(contentType, typeFactory, typeMapping);
        List<JavaType> result = new ArrayList<>();
        for (JavaType contentSubType : contentTypes) {
            JavaType contentSubTypeArray = typeFactory.constructArrayType(contentSubType);
            result.add(contentSubTypeArray);
        }
        return result;
    }

    private static List<JavaType> resolveNonArrayTypes(JavaType javaType, TypeFactory typeFactory,
            TypeMapping typeMapping) {
        List<JavaType> types = new ArrayList<>();
        types.add(javaType);

        Class<?> rawClass = javaType.getRawClass();

        // Jackson annotation mapped subtypes
        JsonSubTypes jsonSubTypes = rawClass.getAnnotation(JsonSubTypes.class);
        if (jsonSubTypes != null) {
            for (JsonSubTypes.Type subType : jsonSubTypes.value()) {
                JavaType javaSubType = typeFactory.constructType(subType.value());
                types.add(javaSubType);
            }
        }

        // custom mapped subtypes
        for (Class<?> mapped : typeMapping.getSubtypes(javaType.getRawClass())) {
            JavaType javaSubType = typeFactory.constructType(mapped);
            types.add(javaSubType);
        }

        return types;
    }

    public static Type firstGenericType(MethodParameter param) {
        return firstGenericType(param.getGenericParameterType(), param.getContainingClass());
    }

    public static Type firstGenericType(Type type, Class<?> containingClass) {
        if (type instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable) type;
            return findTypeFromTypeVariable(tv, containingClass);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type actualArgument = parameterizedType.getActualTypeArguments()[0];
            if (actualArgument instanceof Class) {
                return actualArgument;
            } else if (actualArgument instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) actualArgument;
                return findTypeFromTypeVariable(typeVariable, containingClass);
            }
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            return Object.class;
        }
    }

    public static Type findTypeFromTypeVariable(TypeVariable typeVariable, Class<?> clazz) {
        String variableName = typeVariable.getName();
        Map<TypeVariable, Type> typeMap = GenericTypeResolver.getTypeVariableMap(clazz);
        for (TypeVariable tv : typeMap.keySet()) {
            if (StringUtils.equals(tv.getName(), variableName)) {
                return typeMap.get(tv);
            }
        }
        return Object.class;
    }
}
