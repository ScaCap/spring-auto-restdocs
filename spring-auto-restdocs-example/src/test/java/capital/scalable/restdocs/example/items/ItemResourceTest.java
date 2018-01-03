/*-
 * #%L
 * Spring Auto REST Docs Example Project
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
package capital.scalable.restdocs.example.items;

import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import capital.scalable.restdocs.example.items.ItemResource.Command;
import capital.scalable.restdocs.example.items.ItemResource.CommandResult;
import capital.scalable.restdocs.example.testsupport.MockMvcBase;
import org.junit.Test;
import org.springframework.http.MediaType;

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
                .content("command=increase"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json("{ \"output\": \"Command executed on item 1: increase\" }"));
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
}
