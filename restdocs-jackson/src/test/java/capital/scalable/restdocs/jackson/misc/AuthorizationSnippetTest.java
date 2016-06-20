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

package capital.scalable.restdocs.jackson.misc;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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

        setField(snippet, "expectedName", "authorization");
        setField(snippet, "expectedType", "authorization");
        this.snippet.withContents(equalTo(authorization));

        new AuthorizationSnippet("Resource is public.")
                .document(operationBuilder("authorization")
                        .attribute(AuthorizationSnippet.class.getName(), authorization)
                        .request("http://localhost/test")
                        .build());
    }

    @Test
    public void defaultAuthorization() throws Exception {
        String defaultAuthorization = "Resource is public.";

        setField(snippet, "expectedName", "authorization");
        setField(snippet, "expectedType", "authorization");
        this.snippet.withContents(equalTo(defaultAuthorization));

        new AuthorizationSnippet(defaultAuthorization)
                .document(operationBuilder("authorization")
                        .request("http://localhost/test")
                        .build());
    }
}
