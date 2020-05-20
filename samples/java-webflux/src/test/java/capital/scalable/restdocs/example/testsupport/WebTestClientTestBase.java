/*-
 * #%L
 * Spring Auto REST Docs Java WebFlux Example Project
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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
package capital.scalable.restdocs.example.testsupport;

import static capital.scalable.restdocs.AutoDocumentation.authorization;
import static capital.scalable.restdocs.AutoDocumentation.description;
import static capital.scalable.restdocs.AutoDocumentation.methodAndPath;
import static capital.scalable.restdocs.AutoDocumentation.pathParameters;
import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.requestParameters;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.AutoDocumentation.section;
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.limitJsonArrayLength;
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.replaceBinaryContent;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

import java.util.function.Consumer;

import capital.scalable.restdocs.webflux.WebTestClientInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Required set up code for WebTestClient tests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class WebTestClientTestBase {

    private static final String DEFAULT_AUTHORIZATION = "Resource is public.";

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    protected WebTestClient webTestClient;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        this.webTestClient = WebTestClient.bindToApplicationContext(context)
                .apply(springSecurity()).configureClient()
                .baseUrl("http://localhost:8080/")
                .filter(documentationConfiguration(restDocumentation).snippets()
                        .withDefaults(WebTestClientInitializer.prepareSnippets(context),
                                curlRequest(), httpRequest(), httpResponse(),
                                requestFields(), responseFields(), pathParameters(),
                                requestParameters(), description(), methodAndPath(),
                                section(), authorization(DEFAULT_AUTHORIZATION)))
                .build();
        // TODO: alwaysDo(commonDocumentation())
    }

    protected <T extends ExchangeResult> Consumer<T> commonDocumentation(
                    Snippet... snippets) {
        return document("{class-name}/{method-name}", preprocessRequest(),
                commonResponsePreprocessor(), snippets);
    }

    protected OperationResponsePreprocessor commonResponsePreprocessor() {
        return preprocessResponse(replaceBinaryContent(),
                limitJsonArrayLength(objectMapper), prettyPrint());
    }

    protected ExchangeFilterFunction userToken() {

        return (request, next) -> {
            // If the tests requires setup logic for users, you can place it here.
            // Authorization headers or cookies for users should be added here as well.
            // TODO: enable oAuth
            // String accessToken = getAccessToken("test", "test");
            // request.headers().add("Authorization", "Bearer " + accessToken);
            // documentAuthorization(request, "User access token required.");
            return next.exchange(request);
        };
    }

    private String getAccessToken(String username, String password) {
        String authorization = "Basic "
                        + new String(Base64Utils.encode("app:very_secret".getBytes()));
        String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

        String body = new String(webTestClient.post()
                        .uri(uriBuilder -> new DefaultUriBuilderFactory()
                                        .uriString("/oauth/token").queryParam("username", username)
                                        .queryParam("password", password)
                                        .queryParam("grant_type", "password")
                                        .queryParam("scope", "read write").queryParam("client_id", "app")
                                        .queryParam("client_secret", "very_secret").build())
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).exchange()
                        .expectStatus().isOk().expectHeader().contentType(contentType)
                        .expectBody().jsonPath("$.access_token").isNotEmpty()
                        .jsonPath("$.token_type").isEqualTo("bearer").jsonPath("$.refresh_token")
                        .isNotEmpty().jsonPath("$.expires_in").isNumber().jsonPath("$.scope")
                        .isEqualTo("read write").returnResult().getResponseBody());

        return body.substring(17, 53);
    }
}
