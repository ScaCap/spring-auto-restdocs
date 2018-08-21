/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
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
package capital.scalable.restdocs.example.items

import capital.scalable.restdocs.example.common.Money
import capital.scalable.restdocs.example.constraints.English
import capital.scalable.restdocs.example.constraints.German
import capital.scalable.restdocs.example.constraints.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Simple REST resource with CRUD operations.
 */
@RestController
@RequestMapping("/items")
internal class ItemResource {

    /**
     * Returns item by ID.
     *
     *
     * An example of returning a custom response type.
     *
     * @param id ID of the item.
     * @return response
     */
    @GetMapping("{id}")
    fun getItem(@PathVariable("id") @Id id: String): ItemResponse {
        return if ("1" == id) {
            ITEM
        } else {
            throw NotFoundException()
        }
    }

    /**
     * Lists all items.
     *
     *
     * An example of retuning an array/collection.
     *
     * @return list of all items
     */
    @GetMapping
    fun allItems() = arrayOf(ITEM, CHILD)

    /**
     * Adds new item.
     *
     *
     * An example of accepting a custom type as request body and returning a [ResponseEntity].
     *
     * @param itemUpdate Item information
     * @return response
     */
    @PostMapping
    fun addItem(@RequestBody @Valid itemUpdate: ItemUpdateRequest): ResponseEntity<Void> {
        // New item with unique ID is stored and returned.
        val location = ServletUriComponentsBuilder
                .fromUriString("/items")
                .path("/{id}")
                .buildAndExpand("2")
                .toUri()

        return ResponseEntity
                .created(location)
                .body<Void>(null)
    }

    /**
     * Updates existing item.
     *
     *
     * An example of returning an [HttpEntity].
     *
     * @param id         Item ID.
     * @param itemUpdate Item information.
     * @return response
     */
    @PutMapping("{id}")
    fun updateItem(@PathVariable("id") @Id id: String,
                   @RequestBody @Valid itemUpdate: ItemUpdateRequest): HttpEntity<ItemResponse> {
        return HttpEntity(
                ItemResponse(id, itemUpdate.description, null, null, null, null))
    }

    /**
     * Deletes item.
     * <br></br>
     * Item must exist.
     *
     *
     * Non existing items are ignored
     *
     *
     * An example of using path variables.
     *
     * @param id Item ID
     * @see [path
     * parameters documentation](https://scacap.github.io/spring-auto-restdocs/.snippets-path-parameters)
     */
    @DeleteMapping("{id}")
    fun deleteItem(@PathVariable("id") @Id id: String) {
        // Item with the given ID is deleted.
    }

    /**
     * Retrieves a child of specified item.
     *
     *
     * An example of using parameter validation.
     *
     * @param id      Item ID.
     * @param childId Child ID.
     * @return response
     * @see [constraints
     * documentation](https://scacap.github.io/spring-auto-restdocs/.constraints)
     */
    @GetMapping("{id}/{child}")
    fun getChild(@PathVariable @Id id: String,
                 @PathVariable("child")
                 @Min(value = 1, groups = [(English::class)])
                 @Max(value = 2, groups = [(German::class)])
                 childId: String): ItemResponse {
        return if ("1" == id && "child-1" == childId) {
            CHILD
        } else {
            throw NotFoundException()
        }
    }

    /**
     * Searches for item based on lookup parameters.
     *
     *
     * An example of using Pageable, Page and customized translation for paging text.
     *
     * @param descMatch Lookup on description field.
     * @param hint      Lookup hint.
     * @return response
     * @see [paging documentation](https://scacap.github.io/spring-auto-restdocs/.paging)
     */
    @GetMapping("search")
    fun searchItem(
            @RequestParam("desc") @NotBlank @Size(max = 255) descMatch: String,
            @RequestParam(required = false) @Min(10) @Max(100) hint: Int?,
            page: Pageable): Page<ItemResponse> {
        return if (ITEM.description.contains(descMatch)) {
            PageImpl(listOf(ITEM), page, 1)
        } else {
            PageImpl(emptyList(), page, 0)
        }
    }

    /**
     * Executes a command on all items.
     *
     *
     * An example of having String as request and response body.
     */
    @PostMapping("process")
    fun processAllItems(@RequestBody command: String): String {
        // process request as Command
        return """{"output": "processed"}"""
    }

    /**
     * Executes a command on an item.
     * <br></br>
     * This endpoint demos the basic support for @ModelAttribute.
     *
     *
     * Notes:
     *
     *  * the request body is documented as it would be JSON,
     * but it is actually form-urlencoded
     *  * setting the type manually can help to get the right documentation
     * if the automatic document does not produce the right result.
     *
     * An example of using ModelAttribute.
     *
     * @param itemId Item ID.
     * @title Process One Item
     */
    @PostMapping("{itemId}/process")
    fun processSingleItem(@PathVariable itemId: String,
                          @ModelAttribute command: Command): CommandResult {
        return CommandResult("Command executed on item $itemId: ${command.command}")
    }

    /**
     * Validates metadata.
     *
     *
     * An example of accepting subtypes.
     */
    @PostMapping("validateMetadata")
    fun validateMetadata(@RequestBody metadata: Metadata) {
    }

    /**
     * Clones an item.
     *
     *
     * An example of deprecation.
     *
     */
    @PostMapping("cloneItem")
    @Deprecated("create a new item instead")
    fun cloneItem(@RequestBody data: CloneData) {
    }

    internal data class CloneData(
            /**
             * New item's name
             */
            @Deprecated("")
            val name: String)

    internal class Command {
        /**
         * Command to execute
         */
        @get:NotBlank
        var command: String? = null
    }

    internal class CommandResult(
            /**
             * Log output
             */
            @get:NotBlank
            val output: String)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    internal class NotFoundException : RuntimeException()

    companion object {

        private val DECIMAL = BigDecimal("1.11")
        private val AMOUNT = BigDecimal("3.14")

        private val CHILD = ItemResponse("child-1", "first child", null, null, null, null)

        private val ITEM = ItemResponse("1", "main item", Metadata1("1", "meta1"),
                Attributes("first item", 1, true, DECIMAL, Money.of(AMOUNT, "EUR"), EnumType.ONE),
                listOf(CHILD), arrayOf("top-level"))
    }
}
