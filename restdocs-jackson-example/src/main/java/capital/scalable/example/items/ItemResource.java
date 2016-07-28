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
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import capital.scalable.example.items.ItemResponse.Attributes;
import capital.scalable.example.items.ItemResponse.Metadata;
import org.javamoney.moneta.Money;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Simple REST resource with CRUD operations.
 */
@RestController
@RequestMapping("/items")
public class ItemResource {

    private static final ItemResponse CHILD =
            new ItemResponse("child-1", "first child", null, null, null);

    private static final ItemResponse ITEM =
            new ItemResponse("1", "main item", new Metadata("meta1", 22),
                    new Attributes("first item", 1, true, new BigDecimal("1.11"),
                            Money.of(new BigDecimal("3.14"), "EUR")),
                    singletonList(CHILD));

    /**
     * Returns item by id.
     *
     * @param id id of the item
     */
    @RequestMapping("{id}")
    public ItemResponse getItem(@PathVariable("id") String id) {
        if ("1".equals(id)) {
            return ITEM;
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Lists all items.
     */
    @RequestMapping
    public ItemResponse[] allItems() {
        return new ItemResponse[]{ITEM, CHILD};
    }

    /**
     * Adds new item.
     *
     * @param itemUpdate item information
     */
    @RequestMapping(method = POST)
    public ResponseEntity<Void> addItem(@RequestBody @Valid ItemUpdateRequest itemUpdate)
            throws URISyntaxException {
        // New item with unique ID is stored and returned.
        URI location = ServletUriComponentsBuilder
                .fromUriString("/items")
                .path("/{id}")
                .buildAndExpand("2")
                .toUri();

        return ResponseEntity
                .created(location)
                .body(null);
    }

    /**
     * Updates existing item.
     *
     * @param id         item id
     * @param itemUpdate item information
     */
    @RequestMapping(value = "{id}", method = PUT)
    public ItemResponse updateItem(@PathVariable("id") String id,
            @RequestBody @Valid ItemUpdateRequest itemUpdate) {
        return new ItemResponse(id, itemUpdate.getDescription(), null,null, null);
    }

    @RequestMapping(value = "{id}", method = DELETE)
    public void deleteItem(@PathVariable("id") String id) {
        // Item with the given ID is deleted.
    }

    /**
     * Retrieves a child of specified item.
     *
     * @param id      Item ID.
     * @param childId Child ID.
     */
    @RequestMapping("{id}/{child}")
    public ItemResponse getChild(@PathVariable String id,
            @PathVariable("child") String childId) {
        if ("1".equals(id) && "child-1".equals(childId)) {
            return CHILD;
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Searches for item based on lookup parameters.
     *
     * @param descMatch Lookup on description field.
     * @param hint      Lookup hint.
     */
    @RequestMapping("search")
    public Page<ItemResponse> searchItem(@RequestParam("desc") String descMatch,
            @RequestParam(required = false) Integer hint) {
        if (ITEM.getDescription().contains(descMatch)) {
            return new PageImpl<>(singletonList(ITEM));
        } else {
            return new PageImpl<>(Collections.<ItemResponse>emptyList());
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotFoundException extends RuntimeException {
    }
}
