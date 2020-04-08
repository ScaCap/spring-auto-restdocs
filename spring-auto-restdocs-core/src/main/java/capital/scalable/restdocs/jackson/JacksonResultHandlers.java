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
package capital.scalable.restdocs.jackson;

import static capital.scalable.restdocs.OperationAttributeHelper.initRequestPattern;
import static capital.scalable.restdocs.OperationAttributeHelper.setConstraintReader;
import static capital.scalable.restdocs.OperationAttributeHelper.setHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.setJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.setObjectMapper;
import static capital.scalable.restdocs.OperationAttributeHelper.setTypeMapping;

import capital.scalable.restdocs.constraints.ConstraintReaderImpl;
import capital.scalable.restdocs.i18n.SnippetTranslationManager;
import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.javadoc.JavadocReaderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;

public abstract class JacksonResultHandlers {

    public static ResultHandler prepareJackson(ObjectMapper objectMapper) {
        return new JacksonPreparingResultHandler(objectMapper, new TypeMapping(), SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper, SnippetTranslationResolver translationResolver) {
        return new JacksonPreparingResultHandler(objectMapper, new TypeMapping(), translationResolver, new ResourceBundleConstraintDescriptionResolver());
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper, TypeMapping typeMapping) {
        return new JacksonPreparingResultHandler(objectMapper, typeMapping, SnippetTranslationManager.getDefaultResolver(), new ResourceBundleConstraintDescriptionResolver());
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new JacksonPreparingResultHandler(objectMapper, new TypeMapping(), SnippetTranslationManager.getDefaultResolver(), constraintDescriptionResolver);
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper, TypeMapping typeMapping, SnippetTranslationResolver translationResolver) {
        return new JacksonPreparingResultHandler(objectMapper, typeMapping, translationResolver, new ResourceBundleConstraintDescriptionResolver());
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper,  SnippetTranslationResolver translationResolver, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new JacksonPreparingResultHandler(objectMapper, new TypeMapping(), translationResolver, constraintDescriptionResolver);
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper, TypeMapping typeMapping, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new JacksonPreparingResultHandler(objectMapper, typeMapping, SnippetTranslationManager.getDefaultResolver(), constraintDescriptionResolver);
    }

    public static ResultHandler prepareJackson(ObjectMapper objectMapper, TypeMapping typeMapping, SnippetTranslationResolver translationResolver, ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new JacksonPreparingResultHandler(objectMapper, typeMapping, translationResolver, constraintDescriptionResolver);
    }

    private static class JacksonPreparingResultHandler implements ResultHandler {

        private final ObjectMapper objectMapper;
        private final TypeMapping typeMapping;
        private final SnippetTranslationResolver translationResolver;
        private final ConstraintDescriptionResolver constraintDescriptionResolver;

        public JacksonPreparingResultHandler(ObjectMapper objectMapper, TypeMapping typeMapping, SnippetTranslationResolver translationResolver, ConstraintDescriptionResolver constraintDescriptionResolver) {
            this.objectMapper = new SardObjectMapper(objectMapper);
            this.typeMapping = typeMapping;
            this.translationResolver = translationResolver;
            this.constraintDescriptionResolver = constraintDescriptionResolver;
        }

        @Override
        public void handle(MvcResult result) throws Exception {
            // HandlerMethod is not present in case of invalid endpoint
            // or in case of static resource url
            if (result.getHandler() instanceof HandlerMethod) {
                setHandlerMethod(result.getRequest(), (HandlerMethod) result.getHandler());
            }
            setObjectMapper(result.getRequest(), objectMapper);
            initRequestPattern(result.getRequest());
            setJavadocReader(result.getRequest(), JavadocReaderImpl.createWithSystemProperty());
            setConstraintReader(result.getRequest(), ConstraintReaderImpl.create(objectMapper, translationResolver, constraintDescriptionResolver));
            setTypeMapping(result.getRequest(), typeMapping);
        }
    }
}
