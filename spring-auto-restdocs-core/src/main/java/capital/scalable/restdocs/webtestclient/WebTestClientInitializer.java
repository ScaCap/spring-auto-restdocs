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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.constraints.ConstraintReaderImpl;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.javadoc.JavadocReaderImpl;
import reactor.core.publisher.Mono;

/** Watches the WebTestClient and applies some attributes on each operation. */
public class WebTestClientInitializer implements HandlerResultHandler, Ordered {

	/** The called {@link HandlerMethod}. */
	private HandlerMethod handlerMethod;

	@Override
	public boolean supports(HandlerResult result) {
		// if a handler method is set: remember it
		if (result.getHandler() instanceof HandlerMethod) {
			handlerMethod = (HandlerMethod) result.getHandler();
		}
		// all done - nothing more to do
		return false;
	}

	@Override
	public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
		// nothing to do - handler already registered in #supports(HandlerResult)
		return Mono.empty();
	}

	@Override
	public int getOrder() {
		// make sure that this handler always gets called
		return HIGHEST_PRECEDENCE;
	}

	/**
	 * Prepare Snippets for use with the WebTestClient.
	 * @param context The Spring {@link ApplicationContext}.
	 * @return The Snippet. (Just a dummy, but the WebTestClient gets initialized by this
	 * method)
	 */
	public static Snippet prepareSnippets(ApplicationContext context) {

		// Register an instance of this class as spring bean:
		if (context.getBeansOfType(WebTestClientInitializer.class).isEmpty()) {
			((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(
					WebTestClientInitializer.class.getName(),
					new WebTestClientInitializer());

			// refresh DispatcherHandler to take this new Handler:
			context.getBean(DispatcherHandler.class).setApplicationContext(context);
		}

		// create dummy snippet:
		return operation -> {
			// put HandlerMethod in operation attributes:
			HandlerMethod handlerMethod = context
					.getBean(WebTestClientInitializer.class).handlerMethod;
			operation.getAttributes().put(HandlerMethod.class.getName(), handlerMethod);

			// put ObjectMapper in operation attributes:
			ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
			operation.getAttributes().put(ObjectMapper.class.getName(), objectMapper);

			// create JavadocReader and put it in operation attributes:
			operation.getAttributes().put(JavadocReader.class.getName(),
					JavadocReaderImpl.createWithSystemProperty());

			// create ConstraintReader and put it in operation attributes:
			operation.getAttributes().put(ConstraintReader.class.getName(),
					ConstraintReaderImpl.create(objectMapper));

			// copy attribute to be compatible wit MockMvc:
			operation.getAttributes().put("REQUEST_PATTERN", operation.getAttributes()
					.get("org.springframework.restdocs.urlTemplate"));
		};
	}

}
