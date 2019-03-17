/*-
 * #%L
 * Spring Auto REST Docs Java WebFlux Example Project
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
package capital.scalable.restdocs.example.items;

import capital.scalable.restdocs.example.testsupport.WebTestClientTestBase;
import org.junit.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class ItemResourceTest extends WebTestClientTestBase {

    @Test
    public void getItem() {
        webTestClient.get().uri("/items/1").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.description").isEqualTo("main item")
                .jsonPath("$.attributes.text").isEqualTo("first item")
                .jsonPath("$.attributes.number").isEqualTo(1)
                .jsonPath("$.attributes.bool").isEqualTo(true)
                .jsonPath("$.attributes.decimal").isEqualTo(1.11)
                .jsonPath("$.attributes.amount").isEqualTo("3.14 EUR")
                .jsonPath("$.children").isArray()
                .jsonPath("$.children.length()").isEqualTo(1)
                .jsonPath("$.children[0].id").isEqualTo("child-1")
                .jsonPath("$.children[0].description").isEqualTo("first child")
                .jsonPath("$.children[0].attributes").doesNotExist()
                .jsonPath("$.children[0].children").doesNotExist()
                .consumeWith(commonDocumentation());
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri("/items").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("child-1")
                .consumeWith(commonDocumentation());
    }

    @Test
    public void updateItem() {
        ItemUpdateRequest data = new ItemUpdateRequest();
        data.setDescription("Hot News");
        webTestClient.put().uri("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(data), ItemUpdateRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.description").isEqualTo("Hot News")
                .consumeWith(commonDocumentation());
    }
}
