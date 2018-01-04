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

import static capital.scalable.restdocs.example.items.ItemResponse.EnumType.ONE;
import static java.util.Collections.singletonList;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;

import capital.scalable.restdocs.example.constraints.Id;
import capital.scalable.restdocs.example.items.ItemResponse.Attributes;
import capital.scalable.restdocs.example.items.ItemResponse.Metadata;
import capital.scalable.restdocs.example.items.ItemResponse.Metadata1;
import lombok.Data;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    private static final BigDecimal DECIMAL = new BigDecimal("1.11");
    private static final BigDecimal AMOUNT = new BigDecimal("3.14");

    private static final ItemResponse CHILD =
            new ItemResponse("child-1", "first child", null, null, null, null);

    private static final ItemResponse ITEM =
            new ItemResponse("1", "main item", new Metadata1("1", "meta1"),
                    new Attributes("first item", 1, true, DECIMAL, Money.of(AMOUNT, "EUR"), ONE),
                    singletonList(CHILD), new String[]{"top-level"});

    /**
     * Returns item by ID.
     * <p>
     * An example of returning a custom response type.
     *
     * @param id ID of the item.
     * @return response
     */
    @GetMapping("{id}")
    public ItemResponse getItem(@PathVariable("id") @Id String id) {
        if ("1".equals(id)) {
            return ITEM;
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Lists all items.
     * <p>
     * An example of retuning an array/collection.
     *
     * @return list of all items
     */
    @GetMapping
    public ItemResponse[] allItems() {
        return new ItemResponse[]{ITEM, CHILD};
    }

    /**
     * Adds new item.
     * <p>
     * An example of accepting a custom type as request body and returning a {@link ResponseEntity}.
     *
     * @param itemUpdate Item information
     * @return response
     */
    @PostMapping
    public ResponseEntity<Void> addItem(@RequestBody @Valid ItemUpdateRequest itemUpdate) {
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
     * <p>
     * An example of returning an {@link HttpEntity}.
     *
     * @param id         Item ID.
     * @param itemUpdate Item information.
     * @return response
     */
    @PutMapping("{id}")
    public HttpEntity<ItemResponse> updateItem(@PathVariable("id") @Id String id,
            @RequestBody @Valid ItemUpdateRequest itemUpdate) {
        return new HttpEntity<>(
                new ItemResponse(id, itemUpdate.getDescription(), null, null, null, null));
    }

    /**
     * Deletes item.
     * <br>
     * Item must exist.
     * <p>
     * Non existing items are ignored
     * <p>
     * An example of using path variables.
     *
     * @param id Item ID
     * @see <a href="https://scacap.github.io/spring-auto-restdocs/#snippets-path-parameters">path
     * parameters documentation</a>
     */
    @DeleteMapping("{id}")
    public void deleteItem(@PathVariable("id") @Id String id) {
        // Item with the given ID is deleted.
    }

    /**
     * Retrieves a child of specified item.
     * <p>
     * An example of using parameter validation.
     *
     * @param id      Item ID.
     * @param childId Child ID.
     * @return response
     * @see <a href="https://scacap.github.io/spring-auto-restdocs/#constraints">constraints
     * documentation</a>
     */
    @GetMapping("{id}/{child}")
    public ItemResponse getChild(@PathVariable @Id String id,
            @PathVariable("child")
            @Min(value = 1, groups = English.class)
            @Max(value = 2, groups = German.class) String childId) {
        if ("1".equals(id) && "child-1".equals(childId)) {
            return CHILD;
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Searches for item based on lookup parameters.
     * <p>
     * An example of using Pageable, Page and customized translation for paging text.
     *
     * @param descMatch Lookup on description field.
     * @param hint      Lookup hint.
     * @return response
     * @see <a href="https://scacap.github.io/spring-auto-restdocs/#paging">paging documentation</a>
     */
    @GetMapping("search")
    public Page<ItemResponse> searchItem(
            @RequestParam("desc") @NotBlank @Size(max = 255) String descMatch,
            @RequestParam(required = false) @Min(10) @Max(100) Integer hint,
            Pageable page) {
        if (ITEM.getDescription().contains(descMatch)) {
            return new PageImpl<>(singletonList(ITEM), page, 1);
        } else {
            return new PageImpl<>(Collections.<ItemResponse>emptyList(), page, 0);
        }
    }

    /**
     * Executes a command on all items.
     * <p>
     * An example of having String as request and response body.
     */
    @PostMapping("process")
    public String processAllItems(@RequestBody String command) {
        // process request as Command
        return "{ \"output\": \"processed\" }";
    }

    /**
     * Executes a command on an item.
     * <br>
     * This endpoint demos the basic support for @ModelAttribute.
     * <p>
     * Notes:
     * <ul>
     * <li>the request body is documented as it would be JSON,
     * but it is actually form-urlencoded</li>
     * <li>setting the type manually can help to get the right documentation
     * if the automatic document does not produce the right result.</li>
     * </ul>
     * An example of using ModelAttribute.
     *
     * @param itemId Item ID.
     * @title Process One Item
     */
    @PostMapping("{itemId}/process")
    public CommandResult processSingleItem(@PathVariable String itemId,
            @ModelAttribute Command command) {
        return new CommandResult(
                String.format("Command executed on item %s: %s", itemId, command.getCommand()));
    }

    /**
     * Validates metadata.
     * <p>
     * An example of accepting subtypes.
     */
    @PostMapping("validateMetadata")
    public void validateMetadata(@RequestBody Metadata metadata) {
    }

    /**
     * Clones an item.
     * <p>
     * An example of deprecation.
     *
     * @deprecated create a new item instead
     */
    @Deprecated
    @PostMapping("cloneItem")
    public void cloneItem(@RequestBody CloneData data) {
    }

    static class CloneData {
        /**
         * New item's name
         */
        @Deprecated
        private String name;
    }

    @Data
    static class Command {
        /**
         * Command to execute
         */
        @NotBlank
        private String command;
    }

    @Value
    static class CommandResult {
        /**
         * Log output
         */
        @NotBlank
        private String output;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotFoundException extends RuntimeException {
    }
}
