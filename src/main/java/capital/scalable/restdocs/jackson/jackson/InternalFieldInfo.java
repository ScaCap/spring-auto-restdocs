package capital.scalable.restdocs.jackson.jackson;

class InternalFieldInfo {
    private final Class<?> javaBaseClass;
    private final String javaFieldName;
    private final String jsonFieldPath;
    private final Boolean optional;

    public InternalFieldInfo(Class<?> javaBaseClass, String javaFieldName,
            String jsonFieldPath, Boolean optional) {
        this.javaBaseClass = javaBaseClass;
        this.javaFieldName = javaFieldName;
        this.jsonFieldPath = jsonFieldPath;
        this.optional = optional;
    }

    public Class<?> getJavaBaseClass() {
        return javaBaseClass;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public String getJsonFieldPath() {
        return jsonFieldPath;
    }

    public Boolean getOptional() {
        return optional;
    }
}
