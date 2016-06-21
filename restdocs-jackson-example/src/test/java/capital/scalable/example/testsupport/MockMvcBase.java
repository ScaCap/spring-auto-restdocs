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

package capital.scalable.example.testsupport;

import static capital.scalable.restdocs.jackson.AutoDocumentation.authorization;
import static capital.scalable.restdocs.jackson.AutoDocumentation.description;
import static capital.scalable.restdocs.jackson.AutoDocumentation.methodAndPath;
import static capital.scalable.restdocs.jackson.AutoDocumentation.pathParameters;
import static capital.scalable.restdocs.jackson.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.jackson.AutoDocumentation.requestParameters;
import static capital.scalable.restdocs.jackson.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.jackson.AutoDocumentation.section;
import static capital.scalable.restdocs.jackson.jackson.JacksonResultHandlers.prepareJackson;
import static capital.scalable.restdocs.jackson.misc.AuthorizationSnippet.documentAuthorization;
import static capital.scalable.restdocs.jackson.response.ResponseModifyingPreprocessors.limitJsonArrayLength;
import static capital.scalable.restdocs.jackson.response.ResponseModifyingPreprocessors.replaceBinaryContent;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.curl.CurlDocumentation.curlRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.Filter;

import capital.scalable.example.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

/**
 * Required set up code for MockMvc tests.
 *
 * @author Florian Benz
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
public abstract class MockMvcBase {

    private static final String DEFAULT_AUTHORIZATION = "Resource is public.";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private Filter springSecurityFilterChain;

    protected MockMvc mockMvc;

    @Rule
    public final JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("target/generated-snippets");

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .alwaysDo(prepareJackson(objectMapper))
                .alwaysDo(document("{class-name}/{method-name}",
                        preprocessRequest(), commonResponsePreprocessor()))
                .apply(documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and().snippets()
                        .withDefaults(curlRequest(), httpRequest(), httpResponse(),
                                requestFields(), responseFields(), pathParameters(),
                                requestParameters(), description(), methodAndPath(),
                                section(), authorization(DEFAULT_AUTHORIZATION)))
                .build();
    }

    protected OperationResponsePreprocessor commonResponsePreprocessor() {
        return preprocessResponse(replaceBinaryContent(), limitJsonArrayLength(objectMapper),
                prettyPrint());
    }

    protected RequestPostProcessor userToken() {
        return new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                // If the tests requires setup logic for users, you can place it here.
                // Authorization headers or cookies for users should be added here as well.
                String accessToken;
                try {
                    accessToken = getAccessToken("test", "test");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                request.addHeader("Authorization", "Bearer " + accessToken);
                return documentAuthorization(request, "User access token required.");
            }
        };
    }

    private String getAccessToken(String username, String password) throws Exception {
        String authorization = "Basic "
                + new String(Base64Utils.encode("app:very_secret".getBytes()));
        String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

        String body = mockMvc
                .perform(
                        post("/oauth/token")
                                .header("Authorization", authorization)
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .param("grant_type", "password")
                                .param("scope", "read write")
                                .param("client_id", "app")
                                .param("client_secret", "very_secret"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
                .andExpect(jsonPath("$.refresh_token", is(notNullValue())))
                .andExpect(jsonPath("$.expires_in", is(greaterThan(4000))))
                .andExpect(jsonPath("$.scope", is(equalTo("read write"))))
                .andReturn().getResponse().getContentAsString();

        return body.substring(17, 53);
    }
}
