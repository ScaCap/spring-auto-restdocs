/*-
 * #%L
 * Spring Auto REST Docs Java WebFlux Example Project
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

import static capital.scalable.restdocs.example.items.EnumType.ONE;
import static java.util.Collections.singletonList;

import javax.validation.Valid;
import java.math.BigDecimal;

import capital.scalable.restdocs.example.common.Money;
import capital.scalable.restdocs.example.constraints.Id;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * An example of returning a custom response type using Mono.
     *
     * @param id ID of the item.
     * @return response
     */
    @GetMapping("{id}")
    public Mono<ItemResponse> getItem(@PathVariable("id") @Id String id) {
        return Mono.just(ITEM);
    }

    /**
     * Lists all items.
     * <p>
     * An example of retuning a collection using Flux.
     *
     * @return list of all items
     */
    @GetMapping
    public Flux<ItemResponse> allItems() {
        return Flux.fromArray(new ItemResponse[]{ITEM, CHILD});
    }

    /**
     * Updates existing item.
     * <p>
     * An example of returning a Mono with {@link ResponseEntity}.
     *
     * @param id         Item ID.
     * @param itemUpdate Item information.
     * @return response
     */
    @PutMapping("{id}")
    public Mono<HttpEntity<ItemResponse>> updateItem(@PathVariable("id") @Id String id,
            @RequestBody @Valid ItemUpdateRequest itemUpdate) {
        return Mono.just(ResponseEntity.ok(
                new ItemResponse(id, itemUpdate.getDescription(), null, null, null, null)));
    }
}
