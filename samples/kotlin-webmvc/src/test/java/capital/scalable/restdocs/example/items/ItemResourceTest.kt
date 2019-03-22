/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
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
package capital.scalable.restdocs.example.items

import capital.scalable.restdocs.AutoDocumentation.requestFields
import capital.scalable.restdocs.AutoDocumentation.responseFields
import capital.scalable.restdocs.example.items.ItemResource.Command
import capital.scalable.restdocs.example.items.ItemResource.CommandResult
import capital.scalable.restdocs.example.testsupport.MockMvcBase
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ItemResourceTest : MockMvcBase() {

    @Test
    fun getItem() {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", `is`("1")))
                .andExpect(jsonPath("$.description", `is`("main item")))
                .andExpect(jsonPath("$.attributes.text", `is`("first item")))
                .andExpect(jsonPath("$.attributes.number", `is`(1)))
                .andExpect(jsonPath("$.attributes.bool", `is`(true)))
                .andExpect(jsonPath("$.attributes.decimal", `is`(1.11)))
                .andExpect(jsonPath("$.attributes.amount", `is`("3.14 EUR")))
                .andExpect(jsonPath("$.children").isArray)
                .andExpect(jsonPath("$.children", hasSize<Any>(1)))
                .andExpect(jsonPath("$.children[0].id", `is`("child-1")))
                .andExpect(jsonPath("$.children[0].description", `is`("first child")))
                .andExpect(jsonPath("$.children[0].attributes", nullValue()))
                .andExpect(jsonPath("$.children[0].children", nullValue()))
    }

    @Test
    fun getAllItems() {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$", hasSize<Any>(2)))
                .andExpect(jsonPath("$[*].id", hasItems("1", "child-1")))
    }

    @Test
    fun addItem() {
        mockMvc.perform(post("/items").with(userToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(status().isCreated)
                .andExpect(header().string("location", `is`("/items/2")))
    }

    @Test
    fun updateItem() {
        mockMvc.perform(put("/items/1").with(userToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(jsonPath("$.id", `is`("1")))
                .andExpect(jsonPath("$.description", `is`("Hot News")))
                .andExpect(status().isOk)
    }

    @Test
    fun deleteItem() {
        mockMvc.perform(delete("/items/1").with(userToken()))
                .andExpect(status().isOk)
    }

    @Test
    fun getChildItem() {
        mockMvc.perform(get("/items/1/child-1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", `is`("child-1")))
                .andExpect(jsonPath("$.description", `is`("first child")))
    }

    @Test
    fun searchItems() {
        mockMvc.perform(get("/items/search")
                .param("desc", "main")
                .param("hint", "1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content").isArray)
                .andExpect(jsonPath("$.content", hasSize<Any>(1)))
                .andExpect(jsonPath("$.content[0].id", `is`("1")))
                .andExpect(jsonPath("$.content[0].description", `is`("main item")))
                // example for overriding path and preprocessors
                .andDo(document("{class-name}/search", commonResponsePreprocessor()))
    }

    @Test
    fun processAllItems() {
        mockMvc.perform(post("/items/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"command\": \"cleanup\" }"))
                .andExpect(status().isOk)
                .andExpect(content().json("{ \"output\": \"processed\" }"))
                .andDo(commonDocumentation().document(
                        requestFields().requestBodyAsType(Command::class.java),
                        responseFields().responseBodyAsType(CommandResult::class.java)))
    }

    @Test
    fun processSingleItem() {
        mockMvc.perform(post("/items/{itemId}/process", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("command=increase"))
                .andExpect(status().isOk)
                .andExpect(
                        content().json("""{ "output": "Command executed on item 1: increase" }"""))
    }

    @Test
    fun validateMetadata() {
        mockMvc.perform(post("/items/validateMetadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "type": "1", "tag": "myItem" }"""))
                .andExpect(status().isOk)
    }

    @Test
    fun cloneItem() {
        mockMvc.perform(post("/items/cloneItem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"xyz\" }"))
                .andExpect(status().isOk)
    }

    /**
     * This test demonstrates that Spring REST Docs can be used in a Spring Auto REST Docs setup.
     *
     *
     * All Spring Auto REST Docs snippets are created as well because of the setup in [MockMvcBase].
     * This is not a requirement and one could use a pure Spring REST Docs setup here.
     *
     *
     * The result of the manual documentation below ends up in path-parameters.adoc and response-fields.adoc and
     * not in auto-path-parameters.adoc or auto-request-fields.adoc.
     * So there is no conflict and one is free to decide which snippets are included in the documentation.
     *
     *
     * RestDocumentationRequestBuilders.get is required for Spring REST Docs' pathParameters to work.
     * Spring AUTO Rest Docs works with both MockMvcRequestBuilders and RestDocumentationRequestBuilders.
     */
    @Test
    fun getItemWithRestDocs() {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/items/{id}", 1))
                .andExpect(status().isOk)
                .andDo(document("{class-name}/{method-name}", commonResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("id").description("ID of the item.")),
                        relaxedResponseFields(fieldWithPath("id")
                                .description("There are more fields but only the ID field is documented."))))
    }
}
