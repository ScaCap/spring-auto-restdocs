/*-
 * #%L
 * Spring Auto REST Docs Java Web MVC Example Project
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.example.items;

import static capital.scalable.restdocs.AutoDocumentation.embedded;
import static capital.scalable.restdocs.AutoDocumentation.links;
import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.AutoDocumentation.sectionBuilder;
import static capital.scalable.restdocs.SnippetRegistry.AUTO_AUTHORIZATION;
import static capital.scalable.restdocs.SnippetRegistry.AUTO_REQUEST_FIELDS;
import static capital.scalable.restdocs.SnippetRegistry.AUTO_REQUEST_PARAMETERS;
import static capital.scalable.restdocs.SnippetRegistry.CURL_REQUEST;
import static capital.scalable.restdocs.SnippetRegistry.HTTP_RESPONSE;
import static capital.scalable.restdocs.SnippetRegistry.PATH_PARAMETERS;
import static capital.scalable.restdocs.SnippetRegistry.RESPONSE_FIELDS;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import capital.scalable.restdocs.example.items.HypermediaItemResponse.EmbeddedDocumentation;
import capital.scalable.restdocs.example.items.HypermediaItemResponse.ExtendedLinksDocumentation;
import capital.scalable.restdocs.example.items.ItemResource.Command;
import capital.scalable.restdocs.example.items.ItemResource.CommandResult;
import capital.scalable.restdocs.example.testsupport.MockMvcBase;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class ItemResourceTest extends MockMvcBase {

    @Test
    public void getItem() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.description", is("main item")))
                .andExpect(jsonPath("$.attributes.text", is("first item")))
                .andExpect(jsonPath("$.attributes.number", is(1)))
                .andExpect(jsonPath("$.attributes.bool", is(true)))
                .andExpect(jsonPath("$.attributes.decimal", is(1.11)))
                .andExpect(jsonPath("$.attributes.amount", is("3.14 EUR")))
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].id", is("child-1")))
                .andExpect(jsonPath("$.children[0].description", is("first child")))
                .andExpect(jsonPath("$.children[0].attributes", nullValue()))
                .andExpect(jsonPath("$.children[0].children", nullValue()));
    }

    @Test
    public void getAllItems() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", hasItems("1", "child-1")));
    }

    @Test
    public void addItem() throws Exception {
        mockMvc.perform(post("/items").with(userToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", is("/items/2")));
    }

    @Test
    public void updateItem() throws Exception {
        mockMvc.perform(put("/items/1").with(userToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.description", is("Hot News")))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/1").with(userToken()))
                .andExpect(status().isOk());
    }

    @Test
    public void getChildItem() throws Exception {
        mockMvc.perform(get("/items/1/child-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("child-1")))
                .andExpect(jsonPath("$.description", is("first child")));
    }

    @Test
    public void searchItems() throws Exception {
        mockMvc.perform(get("/items/search")
                .param("desc", "main")
                .param("hint", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("1")))
                .andExpect(jsonPath("$.content[0].description", is("main item")))
                // example for overriding path and preprocessors
                .andDo(document("{class-name}/search", commonResponsePreprocessor()));
    }

    @Test
    public void processAllItems() throws Exception {
        mockMvc.perform(post("/items/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"command\": \"cleanup\" }"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"output\": \"processed\" }"))
                .andDo(commonDocumentation().document(
                        requestFields().requestBodyAsType(Command.class),
                        responseFields().responseBodyAsType(CommandResult.class)));
    }

    @Test
    public void processSingleItem() throws Exception {
        mockMvc.perform(post("/items/{itemId}/process", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("command=increase&tag=processed&page=1&name=myitem"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json("{ \"output\": \"Command executed on item 1: increase, method: POST, " +
                                "tag: processed, data: myitem, errors: 0, " +
                                "Page request [number: 1, size 20, sort: UNSORTED]\" }"));
    }

    @Test
    public void validateMetadata() throws Exception {
        mockMvc.perform(post("/items/validateMetadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"type\": \"1\", \"tag\": \"myItem\" }"))
                .andExpect(status().isOk());
    }

    @Test
    public void cloneItem() throws Exception {
        mockMvc.perform(post("/items/cloneItem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"xyz\" }"))
                .andExpect(status().isOk());
    }

    /**
     * This test demonstrates that Spring REST Docs can be used in a Spring Auto REST Docs setup.
     * <p>
     * All Spring Auto REST Docs snippets are created as well because of the setup in {@link MockMvcBase}.
     * This is not a requirement and one could use a pure Spring REST Docs setup here.
     * <p>
     * The result of the manual documentation below ends up with section snippet using
     * classic Spring REST Docs' path-parameters.adoc and response-fields.adoc.
     * So there is no conflict and one is free to decide which snippets are included in the documentation.
     * <p>
     * RestDocumentationRequestBuilders.get is required for Spring REST Docs' pathParameters to work.
     * Spring AUTO Rest Docs works with both MockMvcRequestBuilders and RestDocumentationRequestBuilders.
     */
    @Test
    public void getItemWithRestDocs() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/items/{id}", 1))
                .andExpect(status().isOk())
                .andDo(commonDocumentation(
                        pathParameters(
                                parameterWithName("id").description("ID of the item.")),
                        relaxedResponseFields(fieldWithPath("id")
                                .description("There are more fields but only the ID field is documented.")),
                        sectionBuilder().snippetNames(
                                AUTO_AUTHORIZATION,
                                PATH_PARAMETERS, // classic snippet
                                AUTO_REQUEST_PARAMETERS,
                                AUTO_REQUEST_FIELDS,
                                RESPONSE_FIELDS, // classic snippet
                                CURL_REQUEST, // classic snippet
                                HTTP_RESPONSE // classic snippet
                        ).build()));
    }

    @Test
    public void getHypermediaItem() throws Exception {
        mockMvc.perform(get("/items/media/1").param("embedded", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.desc", is("hypermedia item")))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.self.href", is("http://localhost:8080/items/media/1?embedded=true")))
                .andExpect(jsonPath("$._links.classicItem").exists())
                .andExpect(jsonPath("$._links.classicItem.href", is("http://localhost:8080/items/1")))
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.children").isArray())
                .andExpect(jsonPath("$._embedded.children[0].id", is("child-1")))
                .andExpect(jsonPath("$._embedded.meta").exists())
                .andExpect(jsonPath("$._embedded.meta.type", is("1")))
                .andExpect(jsonPath("$._embedded.attributes").exists())
                .andExpect(jsonPath("$._embedded.attributes.number", is(1)))
                .andDo(commonDocumentation(
                        links().documentationType(ExtendedLinksDocumentation.class),
                        embedded().documentationType(EmbeddedDocumentation.class)
                ));
    }

    @Test
    public void getItemAsResource() throws Exception {
        mockMvc.perform(get("/items/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.self.href", is("http://localhost:8080/items/resources/1")))
                .andDo(commonDocumentation(
                        links().documentationType(LinksDocumentation.class)
                ));
    }

    @Test
    public void getItemWithFilter() throws Exception {
        mockMvc.perform(get("/items/filtered?name=abc&tag=prod"))
                .andExpect(status().isOk());
    }

    @Test
    public void updateItemWithFilter() throws Exception {
        mockMvc.perform(put("/items/filtered/1").with(userToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(status().isOk());
    }
}
