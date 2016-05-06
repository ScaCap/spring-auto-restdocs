package capital.scalable.restdocs.jackson.javadoc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JavadocReaderImplTest {

    private static final String SOURCE_DIR = JavadocReaderImplTest.class.
            getClassLoader().getResource("json").getPath();

    @Test
    public void resolveFieldComment() {
        JavadocReader javadocReader = new JavadocReaderImpl(SOURCE_DIR);
        String comment = javadocReader.resolveFieldComment(SimpleType.class, "simpleField");
        assertThat(comment, equalTo("Simple field comment"));
    }

    @Test
    public void resolveMethodComment() {
        JavadocReader javadocReader = new JavadocReaderImpl(SOURCE_DIR);
        String comment = javadocReader.resolveMethodComment(SimpleType.class, "simpleMethod");
        assertThat(comment, equalTo("Simple method comment"));
    }

    @Test
    public void resolveMethodParameterComment() {
        JavadocReader javadocReader = new JavadocReaderImpl(SOURCE_DIR);
        String comment = javadocReader.resolveMethodParameterComment(SimpleType.class,
                "simpleMethod", "simpleParameter");
        assertThat(comment, equalTo("Simple parameter comment"));
    }

    @Test
    public void jsonFileDoesNotExist() {
        JavadocReader javadocReader = new JavadocReaderImpl(SOURCE_DIR);

        // json file for class does not exist
        String comment = javadocReader.resolveFieldComment(NotExisting.class, "simpleField2");
        assertThat(comment, is(""));
        comment = javadocReader.resolveMethodComment(NotExisting.class, "simpleMethod");
        assertThat(comment, is(""));
        comment = javadocReader.resolveMethodParameterComment(NotExisting.class, "simpleMethod",
                "simpleParameter");
        assertThat(comment, is(""));
    }

    @Test
    public void jsonNotDocumented() {
        JavadocReader javadocReader = new JavadocReaderImpl(SOURCE_DIR);

        // json file exists but field/method/parameter is not present
        String comment = javadocReader.resolveFieldComment(SimpleType.class, "simpleField2");
        assertThat(comment, is(""));
        comment = javadocReader.resolveMethodComment(SimpleType.class, "simpleMethod2");
        assertThat(comment, is(""));
        comment = javadocReader.resolveMethodParameterComment(SimpleType.class, "simpleMethod",
                "simpleParameter2");
        assertThat(comment, is(""));
        comment = javadocReader.resolveMethodParameterComment(SimpleType.class, "simpleMethod2",
                "simpleParameter2");
        assertThat(comment, is(""));
    }

    // json file in src/test/resources/json
    private static class SimpleType {
        private String simpleField;

        private void simpleMethod(String simpleParameter) {
        }
    }

    private static class NotExisting {
    }
}
