/*-
 * #%L
 * Spring Auto REST Docs Java Web MVC Example Project
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
package capital.scalable.restdocs.webtestclient;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.javadoc.JavadocReader;
import reactor.core.publisher.Mono;

/** Tests for {@link WebTestClientInitializer}. */
public class WebTestClientInitializerTest {

	/**
	 * Test for method {@link WebTestClientInitializer#supports(HandlerResult)}.
	 */
	@Test
	public void supports_withHandlerMethod() {

		// prepare:
		HandlerResult handlerResultMock = mock(HandlerResult.class);
		HandlerMethod handlerMethodMock = mock(HandlerMethod.class);
		Mockito.when(handlerResultMock.getHandler()).thenReturn(handlerMethodMock);

		// perform:
		WebTestClientInitializer testInstance = new WebTestClientInitializer();
		boolean result = testInstance.supports(handlerResultMock);

		// verify:
		assertFalse(result);
		verify(handlerResultMock, atLeast(1)).getHandler();
	}

	/**
	 * Test for method {@link WebTestClientInitializer#supports(HandlerResult)} in the
	 * case that the {@linkplain HandlerResult#getHandler() handler} is no
	 * {@link HandlerMethod}.
	 */
	@Test
	public void supports_withoutHandlerMethod_noException() {

		// prepare:
		WebTestClientInitializer testInstance = new WebTestClientInitializer();
		HandlerResult handlerResultMock = mock(HandlerResult.class);
		String noHandlerMethod = "string value just for unit test";
		Mockito.when(handlerResultMock.getHandler()).thenReturn(noHandlerMethod);

		// perform:
		boolean result = testInstance.supports(handlerResultMock);

		// verify:
		assertFalse(result);
		verify(handlerResultMock, atLeast(1)).getHandler();
	}

	/**
	 * Test for method
	 * {@link WebTestClientInitializer#handleResult(ServerWebExchange, HandlerResult)}.
	 */
	@Test
	public void handleResult_doNothing() {

		// prepare:
		WebTestClientInitializer testInstance = new WebTestClientInitializer();
		ServerWebExchange exchange = mock(ServerWebExchange.class);
		HandlerResult handlerResult = mock(HandlerResult.class);

		// perform:
		Mono<Void> result = testInstance.handleResult(exchange, handlerResult);

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
		HandlerResult handlerResultMock = mock(HandlerResult.class);
		HandlerMethod handlerMethodMock = mock(HandlerMethod.class);
		Mockito.when(handlerResultMock.getHandler()).thenReturn(handlerMethodMock);
		webTestClientInitializer.supports(handlerResultMock);
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
