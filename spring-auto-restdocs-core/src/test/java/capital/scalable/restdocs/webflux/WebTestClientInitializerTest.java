/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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
package capital.scalable.restdocs.webflux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.jackson.TypeMapping;
import capital.scalable.restdocs.javadoc.JavadocReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Tests for {@link WebTestClientInitializer}. */
public class WebTestClientInitializerTest {

    /**
     * Test for method {@link WebTestClientInitializer#supports(Object)}.
     */
    @Test
    public void supports_withHandlerMethod() {

        // prepare:
        HandlerMethod handlerMethod = mock(HandlerMethod.class);

        // perform:
        WebTestClientInitializer testInstance = new WebTestClientInitializer();
        boolean result = testInstance.supports(handlerMethod);

        // verify:
        assertFalse(result);
    }

    /**
     * Test for method {@link WebTestClientInitializer#supports(Object)} in the
     * case that the {@linkplain HandlerResult#getHandler() handler} is no
     * {@link HandlerMethod}.
     */
    @Test
    public void supports_withoutHandlerMethod_noException() {

        // prepare:
        WebTestClientInitializer testInstance = new WebTestClientInitializer();
        String noHandlerMethod = "string value just for unit test";

        // perform:
        boolean result = testInstance.supports(noHandlerMethod);

        // verify:
        assertFalse(result);
    }

    /**
     * Test for method
     * {@link WebTestClientInitializer#handle(ServerWebExchange, Object)}.
     */
    @Test
    public void handleResult_doNothing() {

        // prepare:
        WebTestClientInitializer testInstance = new WebTestClientInitializer();
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        HandlerResult handlerResult = mock(HandlerResult.class);

        // perform:
        Mono<HandlerResult> result = testInstance.handle(exchange, handlerResult);

        // verify:
        assertNull(result.block(Duration.ZERO));
    }

    /**
     * Test for method {@link WebTestClientInitializer#getOrder()} .
     */
    @Test
    public void getOrder_highestPrecedence() {

        // perform & verify:
        assertEquals(Ordered.HIGHEST_PRECEDENCE,
                new WebTestClientInitializer().getOrder());
    }

    /**
     * Test for method
     * {@link WebTestClientInitializer#prepareSnippets(ApplicationContext)}.
     * @throws IOException See {@link Snippet#document(Operation)}.
     */
    @Test
    public void prepareSnippets() throws IOException {

        // prepare:
        ConfigurableApplicationContext contextMock = mock(
                ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory registryMock = mock(
                ConfigurableListableBeanFactory.class);
        when(contextMock.getBeanFactory()).thenReturn(registryMock);
        DispatcherHandler dispatcherHandlerMock = mock(DispatcherHandler.class);
        when(contextMock.getBean(eq(DispatcherHandler.class)))
                .thenReturn(dispatcherHandlerMock);
        WebTestClientInitializer webTestClientInitializer = new WebTestClientInitializer();
        HandlerMethod handlerMethodMock = mock(HandlerMethod.class);
        webTestClientInitializer.supports(handlerMethodMock);
        when(contextMock.getBean(eq(WebTestClientInitializer.class)))
                .thenReturn(webTestClientInitializer);
        when(contextMock.getBean(eq(ObjectMapper.class)))
                .thenReturn(mock(ObjectMapper.class));
        when(contextMock.getBean(eq(JavadocReader.class)))
                .thenReturn(mock(JavadocReader.class));
        when(contextMock.getBean(eq(ConstraintReader.class)))
                .thenReturn(mock(ConstraintReader.class));
        Operation operationMock = mock(Operation.class);
        Map<String, Object> attributes = new HashMap<>();
        when(operationMock.getAttributes()).thenReturn(attributes);

        // perform:
        Snippet snippet = WebTestClientInitializer.prepareSnippets(contextMock);
        snippet.document(operationMock);

        // verify:
        assertNotNull(attributes.get(HandlerMethod.class.getName()));
        assertEquals(handlerMethodMock, attributes.get(HandlerMethod.class.getName()));
        assertNotNull(attributes.get(ObjectMapper.class.getName()));
        assertNotNull(attributes.get(JavadocReader.class.getName()));
        assertNotNull(attributes.get(ConstraintReader.class.getName()));
        assertNotNull(attributes.get(TypeMapping.class.getName()));
    }

    /**
     * Test for method
     * {@link WebTestClientInitializer#prepareSnippets(ApplicationContext)} in case of
     * multiple invokes.
     * @throws IOException See {@link Snippet#document(Operation)}.
     */
    @Test
    public void prepareSnippets_multipleInvokes_singleInstance() throws IOException {

        // prepare:
        ConfigurableApplicationContext contextMock = mock(
                ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory registryMock = mock(
                ConfigurableListableBeanFactory.class);
        Map<String, WebTestClientInitializer> webTestClientInitializerBeans = new HashMap<>();
        Mockito.doAnswer(invocation -> {
            webTestClientInitializerBeans.put(invocation.getArgumentAt(0, String.class),
                    invocation.getArgumentAt(1, WebTestClientInitializer.class));
            return null;
        }).when(registryMock).registerSingleton(any(), any());
        when(contextMock.getBeansOfType(eq(WebTestClientInitializer.class)))
                .then(invocation -> webTestClientInitializerBeans);
        when(contextMock.getBeanFactory()).thenReturn(registryMock);
        DispatcherHandler dispatcherHandlerMock = mock(DispatcherHandler.class);
        when(contextMock.getBean(eq(DispatcherHandler.class)))
                .thenReturn(dispatcherHandlerMock);
        Operation operationMock = mock(Operation.class);
        WebTestClientInitializer webTestClientInitializer = new WebTestClientInitializer();
        HandlerResult handlerResultMock = mock(HandlerResult.class);
        HandlerMethod handlerMethodMock = mock(HandlerMethod.class);
        Mockito.when(handlerResultMock.getHandler()).thenReturn(handlerMethodMock);
        webTestClientInitializer.supports(handlerResultMock);
        when(contextMock.getBean(eq(WebTestClientInitializer.class)))
                .thenReturn(webTestClientInitializer);

        // perform:
        Snippet snippet1 = WebTestClientInitializer.prepareSnippets(contextMock);
        snippet1.document(operationMock);
        Snippet snippet2 = WebTestClientInitializer.prepareSnippets(contextMock);
        snippet2.document(operationMock);

        // verify:
        assertNotNull(snippet1);
        assertNotNull(snippet2);
        verify(registryMock).registerSingleton(any(), any());
    }

}
