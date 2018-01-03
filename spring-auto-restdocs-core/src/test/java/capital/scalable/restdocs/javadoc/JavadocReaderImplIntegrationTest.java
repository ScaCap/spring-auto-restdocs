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
package capital.scalable.restdocs.javadoc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JavadocReaderImplIntegrationTest {

    @Test
    public void resolveComments() {
        JavadocReader javadocReader =
                JavadocReaderImpl.createWithSystemProperty(); // using dir from pom.xml
        String comment = javadocReader.resolveFieldComment(IntegrationType.class, "usefulField");
        assertThat(comment, equalTo("Very useful field"));

        comment = javadocReader.resolveFieldTag(IntegrationType.class, "usefulField", "see");
        assertThat(comment, equalTo("other useful fields"));

        comment = javadocReader.resolveMethodComment(IntegrationType.class, "dummyMethod");
        // line breaks are not handled here - pure javadoc
        assertThat(comment, equalTo("Very useful method<br>\n with new line"));

        comment = javadocReader.resolveMethodTag(IntegrationType.class, "dummyMethod", "title");
        assertThat(comment, equalTo("My Dummy Method"));

        comment = javadocReader.resolveMethodComment(IntegrationType.class, "dummyMethod2");
        assertThat(comment, equalTo(""));

        comment = javadocReader.resolveMethodParameterComment(IntegrationType.class, "dummyMethod",
                "kindaParameter");
        assertThat(comment, equalTo("mandatory param"));
    }

    /**
     * A complex integration type
     */
    // actually parsed by doclet
    static class IntegrationType {
        /**
         * Very useful field
         *
         * @see other useful fields
         */
        private String usefulField;

        /**
         * Very useful method<br>
         * with new line
         *
         * @param kindaParameter mandatory param
         * @title My Dummy Method
         */
        private void dummyMethod(String kindaParameter) {
        }

        private void dummyMethod2() {
        }
    }
}
