package capital.scalable.restdocs.jackson.javadoc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JavadocReaderImplIntegrationTest {

    @Test
    public void resolveComments() {
        JavadocReader javadocReader = new JavadocReaderImpl(); // using dir from pom.xml
        String comment = javadocReader.resolveFieldComment(IntegrationType.class, "usefulField");
        assertThat(comment, equalTo("Very useful field"));

        comment = javadocReader.resolveMethodComment(IntegrationType.class, "dummyMethod");
        assertThat(comment, equalTo("Very useful method"));

        comment = javadocReader.resolveMethodParameterComment(IntegrationType.class, "dummyMethod",
                "kindaParameter");
        assertThat(comment, equalTo("manatory param"));
    }

    /**
     * A complex integration type
     */
    // actually parsed by doclet
    static class IntegrationType {
        /**
         * Very useful field
         */
        private String usefulField;

        /**
         * Very useful method
         *
         * @param kindaParameter manatory param
         */
        private void dummyMethod(String kindaParameter) {
        }
    }
}
