package capital.scalable.restdocs.jackson.javadoc;

public interface JavadocReader {
    String resolveFieldComment(Class<?> javaBaseClass, String javaFieldName);

    String resolveMethodComment(Class<?> javaBaseClass, String javaMethodName);

    String resolveMethodParameterComment(Class<?> javaBaseClass, String javaMethodName,
            String javaParameterName);
}
