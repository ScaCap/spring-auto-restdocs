/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.postman;

import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getPostmanCollection;
import static capital.scalable.restdocs.OperationAttributeHelper.getRequestMethod;
import static capital.scalable.restdocs.misc.DescriptionSnippet.resolveDescription;
import static capital.scalable.restdocs.section.SectionSnippet.resolveTitle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.postman.PostmanCollection.Header;
import capital.scalable.restdocs.postman.PostmanCollection.Item;
import capital.scalable.restdocs.postman.PostmanCollection.Query;
import capital.scalable.restdocs.postman.PostmanCollection.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PostmanSnippet implements Snippet {

    private static List<String> SKIPPED_REQUEST_HEADERS = Arrays.asList(new String[] {
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.HOST,
            HttpHeaders.CONTENT_LENGTH
    });

    @Override
    public void document(Operation operation) throws IOException {
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        if (handlerMethod == null) {
            return;
        }

        JavadocReader javadocReader = getJavadocReader(operation);
        OperationRequest request = operation.getRequest();
        UriComponents uri = UriComponentsBuilder.fromUri(request.getUri()).build();
        OperationResponse response = operation.getResponse();

        PostmanCollection col = getPostmanCollection(operation);
        Item item1 = new Item();
        col.item.add(item1);

        item1.name = resolveTitle(handlerMethod, javadocReader);
        item1.request.method = getRequestMethod(operation);
        item1.request.description = resolveDescription(operation, handlerMethod, javadocReader);
        request.getHeaders().entrySet().stream()
                .filter(entry -> !SKIPPED_REQUEST_HEADERS.contains(entry.getKey()))
                .forEach(entry -> entry.getValue().forEach(value ->
                        item1.request.header.add(new Header(entry.getKey(), value))));
        item1.request.body.mode = "raw";
        item1.request.body.raw = request.getContentAsString();
        item1.request.url.raw = uri.toString();
        item1.request.url.protocol = uri.getScheme();
        item1.request.url.host.add(uri.getHost());
        item1.request.url.port = String.valueOf(uri.getPort());
        item1.request.url.path.addAll(uri.getPathSegments());
        uri.getQueryParams().forEach((key, list) ->
                list.forEach(value -> item1.request.url.query.add(new Query(key, value))));

        Response resp = new Response();
        item1.response.add(resp);
        resp.name = "Example response";
        resp.body = response.getContentAsString();
        resp.status = response.getStatus().getReasonPhrase();
        resp.code = response.getStatus().value();
        response.getHeaders().entrySet().stream()
                .forEach(entry -> entry.getValue().forEach(value ->
                        resp.header.add(new Header(entry.getKey(), value))));
        resp.originalRequest = item1.request;
    }
}
