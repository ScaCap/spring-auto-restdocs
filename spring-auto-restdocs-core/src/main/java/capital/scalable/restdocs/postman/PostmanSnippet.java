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
import static capital.scalable.restdocs.OperationAttributeHelper.getRequestMethod;
import static capital.scalable.restdocs.misc.DescriptionSnippet.resolveDescription;
import static capital.scalable.restdocs.section.SectionSnippet.resolveTitle;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.postman.PostmanCollection.Header;
import capital.scalable.restdocs.postman.PostmanCollection.Item;
import capital.scalable.restdocs.postman.PostmanCollection.Query;
import capital.scalable.restdocs.postman.PostmanCollection.Request;
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

    private PostmanCollection collection;

    public PostmanSnippet(PostmanCollection collection) {
        this.collection = collection;
    }

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
        OperationRequest operationRequest = operation.getRequest();
        UriComponents uri = UriComponentsBuilder.fromUri(operationRequest.getUri()).build();
        OperationResponse response = operation.getResponse();

        String name = resolveTitle(handlerMethod, javadocReader);

        Item item1 = new Item(name);
        collection.item.add(item1);

        Request request = new Request();
        item1.request = request;
        Response resp = new Response();
        item1.response.add(resp);

        request.method = getRequestMethod(operation);
        request.description = resolveDescription(operation, handlerMethod, javadocReader);
        request.header = requestHeaders(operationRequest, request);
        request.url.query = queryParams(uri, request);

        request.body.mode = "raw";
        request.body.raw = operationRequest.getContentAsString();

        request.url.raw = uri.toString();
        request.url.protocol = uri.getScheme();
        request.url.host.add(uri.getHost());
        request.url.port = String.valueOf(uri.getPort());
        request.url.path.addAll(uri.getPathSegments());

        resp.name = "Example response";
        resp.originalRequest = request;
        resp.header = responseHeaders(response, resp);
        resp.body = response.getContentAsString();
        resp.status = response.getStatus().getReasonPhrase();
        resp.code = response.getStatus().value();
    }

    private List<Header> responseHeaders(OperationResponse response, Response resp) {
        return response.getHeaders().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> new Header(entry.getKey(), value)))
                .collect(toList());
    }

    // TODO using auto-params
    private List<Query> queryParams(UriComponents uri, Request request) {
        return uri.getQueryParams().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> new Query(entry.getKey(), value, "")))
                .collect(toList());
    }

    private List<Header> requestHeaders(OperationRequest operationRequest, Request request) {
        return operationRequest.getHeaders().entrySet().stream()
                .filter(entry -> !SKIPPED_REQUEST_HEADERS.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> new Header(entry.getKey(), value)))
                .collect(toList());
    }
}
