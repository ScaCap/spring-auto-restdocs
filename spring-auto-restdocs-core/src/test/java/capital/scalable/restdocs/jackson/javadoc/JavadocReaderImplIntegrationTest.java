/*
 * Copyright 2016 the original author or authors.
 *
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
 */

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
