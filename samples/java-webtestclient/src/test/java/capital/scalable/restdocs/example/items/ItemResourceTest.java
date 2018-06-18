/*-
 * #%L
 * Spring Auto REST Docs Java WebTestClient Example Project
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
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.DefaultUriBuilderFactory;

import capital.scalable.restdocs.example.items.ItemResource.Command;
import capital.scalable.restdocs.example.items.ItemResource.CommandResult;
import capital.scalable.restdocs.example.testsupport.WebTestClientTestBase;
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
    public void addItem() {
		ItemUpdateRequest data = new ItemUpdateRequest();
		data.setDescription("Hot News");
        webTestClient.mutate().filter(userToken()).build().post().uri("/items")
                .contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(data), ItemUpdateRequest.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().valueEquals("location","http://localhost:8080/items/2")
				.expectBody()
				.consumeWith(commonDocumentation());
    }

    @Test
    public void updateItem() {
		ItemUpdateRequest data = new ItemUpdateRequest();
		data.setDescription("Hot News");
        webTestClient.mutate().filter(userToken()).build().put().uri("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(data), ItemUpdateRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.description"). isEqualTo("Hot News")
				.consumeWith(commonDocumentation());
    }

    @Test
    public void deleteItem() {
        webTestClient.mutate().filter(userToken()).build().delete().uri("/items/{id}", 1)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(commonDocumentation());
    }

    @Test
    public void getChildItem() {
        webTestClient.get().uri("/items/{id}/{child}", 1, "child-1")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
                .jsonPath("$.id").isEqualTo("child-1")
                .jsonPath("$.description").isEqualTo("first child")
				.consumeWith(commonDocumentation());
    }

    @Test
    public void searchItems() {
        webTestClient.get()
				.uri(uriBuilder -> new DefaultUriBuilderFactory().uriString("/items/search")
						.queryParam("desc", "main")
						.queryParam("hint", "1")
				.build())
				.exchange()
                .expectStatus().isOk()
				.expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].id").isEqualTo("1")
                .jsonPath("$.content[0].description").isEqualTo("main item")
                // example for overriding path and preprocessors
				.consumeWith(document("{class-name}/search", commonResponsePreprocessor()));
    }

    @Test
    public void processAllItems() {
		Map<String,String> data = new HashMap<>();
		data.put("command", "cleanup");
        webTestClient.post().uri("/items/process")
                .contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(data),Map.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.output").isEqualTo("processed")
                .consumeWith(commonDocumentation(
                        requestFields().requestBodyAsType(Command.class),
                        responseFields().responseBodyAsType(CommandResult.class)));
    }

    @Test
    public void processSingleItem() {
		webTestClient.post().uri("/items/{itemId}/process", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData("command", "increase"))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
                .jsonPath("$.output").isEqualTo("Command executed on item 1: increase")
				.consumeWith(commonDocumentation());
    }

    @Test
    public void validateMetadata() {
        webTestClient.post().uri("/items/validateMetadata")
                .contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject("{ \"type\": \"1\", \"tag\": \"myItem\" }"))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(commonDocumentation());
    }

    @Test
    public void cloneItem() {
        webTestClient.post().uri("/items/cloneItem")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{ \"name\": \"xyz\" }"))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(commonDocumentation());
    }

}
