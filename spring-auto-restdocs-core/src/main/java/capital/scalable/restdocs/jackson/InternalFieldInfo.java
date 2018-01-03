package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.databind.JavaType;

class InternalFieldInfo {
    private final Class<?> javaBaseClass;
    private final String javaFieldName;
    private final JavaType javaFieldType;
    private final String jsonFieldPath;
    private final boolean shouldExpand;

    public InternalFieldInfo(Class<?> javaBaseClass, String javaFieldName, JavaType javaFieldType,
            String jsonFieldPath, boolean shouldExpand) {
        this.javaBaseClass = javaBaseClass;
        this.javaFieldName = javaFieldName;
        this.javaFieldType = javaFieldType;
        this.jsonFieldPath = jsonFieldPath;
        this.shouldExpand = shouldExpand;
    }

    public Class<?> getJavaBaseClass() {
        return javaBaseClass;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public JavaType getJavaFieldType() {
        return javaFieldType;
    }

    public String getJsonFieldPath() {
        return jsonFieldPath;
    }

    public boolean shouldExpand() {
        return shouldExpand;
    }
}
