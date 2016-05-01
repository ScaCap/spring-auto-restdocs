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

package capital.scalable.example.items;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import capital.scalable.example.testsupport.MockMvcBase;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * @author Florian Benz
 */
public class ItemResourceTest extends MockMvcBase {

    @Test
    public void testGetItem() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.description", is("main item")))
                .andExpect(jsonPath("$.attributes.text", is("first item")))
                .andExpect(jsonPath("$.attributes.number", is(1)))
                .andExpect(jsonPath("$.attributes.bool", is(true)))
                .andExpect(jsonPath("$.attributes.decimal", is(1.11)))
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].id", is("child-1")))
                .andExpect(jsonPath("$.children[0].description", is("first child")))
                .andExpect(jsonPath("$.children[0].attributes", nullValue()))
                .andExpect(jsonPath("$.children[0].children", nullValue()))
                .andDo(document("items/get"));
    }

    @Test
    public void testAllItems() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", hasItems("1", "child-1")))
                .andDo(document("items/all"));
    }

    @Test
    public void testAddItem() throws Exception {
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", is("/items/2")))
                .andDo(document("items/add"));
    }

    @Test
    public void testUpdateItem() throws Exception {
        mockMvc.perform(put("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Hot News\"}"))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.description", is("Hot News")))
                .andExpect(status().isOk())
                .andDo(document("items/update"));
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isOk())
                .andDo(document("items/delete"));
    }

    @Test
    public void testGetChildItem() throws Exception {
        mockMvc.perform(get("/items/1/child-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("child-1")))
                .andExpect(jsonPath("$.description", is("first child")))
                .andDo(document("items/child"));
    }

    @Test
    public void testSearchItem() throws Exception {
        mockMvc.perform(get("/items/search?desc=main&hint=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is("1")))
                .andExpect(jsonPath("$.[0].description", is("main item")))
                .andDo(document("items/search"));
    }
}
