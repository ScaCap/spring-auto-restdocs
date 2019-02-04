/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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
package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_AUTHORIZATION;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;

public class AuthorizationSnippetTest extends AbstractSnippetTests {

    public AuthorizationSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void authorization() throws Exception {
        String authorization = "User access token required.";

        this.snippets.expect(AUTO_AUTHORIZATION)
                .withContents(equalTo(authorization));

        new AuthorizationSnippet("Resource is public.")
                .document(operationBuilder
                        .attribute(AuthorizationSnippet.class.getName(), authorization)
                        .request("http://localhost/test")
                        .build());
    }

    @Test
    public void defaultAuthorization() throws Exception {
        String defaultAuthorization = "Resource is public.";

        this.snippets.expect(AUTO_AUTHORIZATION)
                .withContents(equalTo(defaultAuthorization));

        new AuthorizationSnippet(defaultAuthorization)
                .document(operationBuilder
                        .request("http://localhost/test")
                        .build());
    }
}
