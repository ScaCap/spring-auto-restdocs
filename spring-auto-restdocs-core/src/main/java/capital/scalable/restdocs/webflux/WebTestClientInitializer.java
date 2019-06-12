/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.webflux;

import capital.scalable.restdocs.constraints.ConstraintReader;
import capital.scalable.restdocs.constraints.ConstraintReaderImpl;
import capital.scalable.restdocs.i18n.SnippetTranslationManager;
import capital.scalable.restdocs.jackson.TypeMapping;
import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.javadoc.JavadocReaderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Watches the WebTestClient and applies some attributes on each operation.
 */
public class WebTestClientInitializer implements HandlerAdapter, Ordered {

    /**
     * The called {@link HandlerMethod}.
     */
    private HandlerMethod handlerMethod;

    @Override
    public boolean supports(Object handler) {
        // if a handler method is set: remember it
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        }
        // all done - nothing more to do
        return false;
    }

    @Override
    public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
        // nothing to do - handler already registered in #supports(Object)
        return Mono.empty();
    }

    @Override
    public int getOrder() {
        // make sure that this handler always gets called
        return HIGHEST_PRECEDENCE;
    }

    /**
     * Prepare Snippets for use with the WebTestClient.
     *
     * @param context The Spring {@link ApplicationContext}.
     * @return The Snippet. (Just a dummy, but the WebTestClient gets initialized by this
     * method)
     */
    public static Snippet prepareSnippets(ApplicationContext context) {
        return prepareSnippets(context, new TypeMapping());
    }

    /**
     * Prepare Snippets for use with the WebTestClient.
     *
     * @param context     The Spring {@link ApplicationContext}.
     * @param typeMapping custom type mappings
     * @return The Snippet. (Just a dummy, but the WebTestClient gets initialized by this
     * method)
     */
    public static Snippet prepareSnippets(ApplicationContext context, TypeMapping typeMapping) {

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
                    ConstraintReaderImpl.create(objectMapper, SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver()));

            // create TypeMapping and put it in operation attributes:
            operation.getAttributes().put(TypeMapping.class.getName(),
                    typeMapping);

            // copy attribute to be compatible wit MockMvc:
            String requestPattern = (String) operation.getAttributes()
                    .get("org.springframework.restdocs.urlTemplate");
            if (StringUtils.isNotEmpty(requestPattern)) {
                operation.getAttributes().put("REQUEST_PATTERN", requestPattern);
            } else if (operation.getRequest() != null) {
                String path = operation.getRequest().getUri().getPath();
                String query = operation.getRequest().getUri().getQuery();
                operation.getAttributes().put("REQUEST_PATTERN",
                        path + (StringUtils.isNotEmpty(query) ? "?" + query : ""));
            }
        };
    }

}
