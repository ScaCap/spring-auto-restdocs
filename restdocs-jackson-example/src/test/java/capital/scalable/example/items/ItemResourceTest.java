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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        mockMvc.perform(get("/items/someId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.information", is("Information for someId")))
                .andDo(document("items/get"));
    }

    @Test
    public void testAllItems() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItems("0", "1", "2", "50", "99")))
                .andDo(document("items/all"));
    }

    @Test
    public void testAddItem() throws Exception {
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"information\":\"Hot News\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.information", is("Hot News")))
                .andDo(document("items/add"));
    }

    @Test
    public void testUpdateItem() throws Exception {
        mockMvc.perform(put("/items/someId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"information\":\"Hot News\"}"))
                .andExpect(status().isOk())
                .andDo(document("items/update"));
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/someId"))
                .andExpect(status().isOk())
                .andDo(document("items/delete"));
    }
}
