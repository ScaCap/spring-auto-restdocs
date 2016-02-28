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

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import capital.scalable.example.items.ItemResponse.NestedInformation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple REST resource with CRUD operations.
 *
 * @author Florian Benz
 */
@RestController
@RequestMapping("/items")
public class ItemResource {

    @RequestMapping("{id}")
    public ItemResponse getItem(@PathVariable("id") String id) {
        return new ItemResponse(id, "Information for " + id, new NestedInformation("text", 1, true),
                singletonList(new NestedInformation("more text", 42, false)));
    }

    @RequestMapping()
    public List<ItemResponse> allItems() {
        List<ItemResponse> items = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            items.add(new ItemResponse("" + i, "Information for " + i, null, null));
        }
        return items;
    }

    @RequestMapping(method = POST)
    public ItemResponse addItem(@RequestBody @Valid ItemUpdateRequest itemUpdate) {
        // New item with unique ID is stored and returned.
        return new ItemResponse("newRandomId", itemUpdate.getInformation(), null, null);
    }

    @RequestMapping(value = "{id}", method = PUT)
    public void updateItem(@PathVariable("id") String id,
            @RequestBody @Valid ItemUpdateRequest itemUpdate) {
        // Item with the given ID is updated.
    }

    @RequestMapping(value = "{id}", method = DELETE)
    public void deleteItem(@PathVariable("id") String id) {
        // Item with the given ID is deleted.
    }
}
