package capital.scalable.restdocs.javadoc;

public interface JavadocReader {
    String resolveFieldComment(Class<?> javaBaseClass, String javaFieldName);

    String resolveFieldTag(Class<?> javaBaseClass, String javaFieldName, String tagName);

    String resolveMethodComment(Class<?> javaBaseClass, String javaMethodName);

    String resolveMethodParameterComment(Class<?> javaBaseClass, String javaMethodName,
            String javaParameterName);

    String resolveMethodTag(Class<?> javaBaseClass, String javaMethodName, String tagName);
}
