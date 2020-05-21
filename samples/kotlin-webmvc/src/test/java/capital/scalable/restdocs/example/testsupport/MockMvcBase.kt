/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
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
package capital.scalable.restdocs.example.testsupport

import capital.scalable.restdocs.AutoDocumentation.authorization
import capital.scalable.restdocs.AutoDocumentation.description
import capital.scalable.restdocs.AutoDocumentation.methodAndPath
import capital.scalable.restdocs.AutoDocumentation.modelAttribute
import capital.scalable.restdocs.AutoDocumentation.pathParameters
import capital.scalable.restdocs.AutoDocumentation.requestFields
import capital.scalable.restdocs.AutoDocumentation.requestParameters
import capital.scalable.restdocs.AutoDocumentation.responseFields
import capital.scalable.restdocs.AutoDocumentation.section
import capital.scalable.restdocs.jackson.JacksonResultHandlers.prepareJackson
import capital.scalable.restdocs.misc.AuthorizationSnippet.documentAuthorization
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors.limitJsonArrayLength
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors.replaceBinaryContent
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.cli.CliDocumentation.curlRequest
import org.springframework.restdocs.http.HttpDocumentation.httpRequest
import org.springframework.restdocs.http.HttpDocumentation.httpResponse
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.Base64Utils
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import javax.servlet.Filter

private const val DEFAULT_AUTHORIZATION = "Resource is public."

/**
 * Required set up code for MockMvc tests.
 */
@RunWith(SpringRunner::class)
@SpringBootTest
abstract class MockMvcBase {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var springSecurityFilterChain: Filter

    @Autowired
    private lateinit var requestMappingHandlerAdapter: RequestMappingHandlerAdapter

    protected lateinit var mockMvc: MockMvc

    @get:Rule
    val restDocumentation = JUnitRestDocumentation()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilters<DefaultMockMvcBuilder>(springSecurityFilterChain)
            .alwaysDo<DefaultMockMvcBuilder>(prepareJackson(objectMapper))
            .alwaysDo<DefaultMockMvcBuilder>(commonDocumentation())
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation)
                .uris()
                .withScheme("http")
                .withHost("localhost")
                .withPort(8080)
                .and().snippets()
                .withDefaults(curlRequest(), httpRequest(), httpResponse(),
                    requestFields(), responseFields(), pathParameters(),
                    requestParameters(), description(), methodAndPath(),
                    section(), authorization(DEFAULT_AUTHORIZATION),
                    modelAttribute(requestMappingHandlerAdapter.getArgumentResolvers())))
            .build()
    }

    protected fun commonDocumentation(): RestDocumentationResultHandler {
        return document("{class-name}/{method-name}",
            preprocessRequest(), commonResponsePreprocessor())
    }

    protected fun commonResponsePreprocessor(): OperationResponsePreprocessor {
        return preprocessResponse(replaceBinaryContent(), limitJsonArrayLength(objectMapper),
            prettyPrint())
    }

    protected fun userToken(): RequestPostProcessor {
        return RequestPostProcessor { request ->
            // If the tests requires setup logic for users, you can place it here.
            // Authorization headers or cookies for users should be added here as well.
            val accessToken: String
            try {
                accessToken = getAccessToken("test", "test")
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            request.addHeader("Authorization", "Bearer $accessToken")
            documentAuthorization(request, "User access token required.")
        }
    }

    @Throws(Exception::class)
    private fun getAccessToken(username: String, password: String): String {
        val authorization = "Basic " + String(Base64Utils.encode("app:very_secret".toByteArray()))
        val contentType = MediaType.APPLICATION_JSON.toString() + ";charset=UTF-8"

        val body = mockMvc
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
            .andExpect(status().isOk)
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.access_token", `is`(notNullValue())))
            .andExpect(jsonPath("$.token_type", `is`(equalTo("bearer"))))
            .andExpect(jsonPath("$.refresh_token", `is`(notNullValue())))
            .andExpect(jsonPath("$.expires_in", `is`(greaterThan(4000))))
            .andExpect(jsonPath("$.scope", `is`(equalTo("read write"))))
            .andReturn().response.contentAsString

        return body.substring(17, 53)
    }
}
