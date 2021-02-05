/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.determineTemplateFormatting;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getTranslationResolver;
import static capital.scalable.restdocs.SnippetRegistry.AUTO_DESCRIPTION;
import static capital.scalable.restdocs.javadoc.JavadocUtil.convertFromJavadoc;
import static capital.scalable.restdocs.util.FormatUtil.addDot;
import static capital.scalable.restdocs.util.FormatUtil.join;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public class DescriptionSnippet extends TemplatedSnippet {

    public DescriptionSnippet() {
        super(AUTO_DESCRIPTION, null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        Map<String, Object> model = defaultModel();
        if (handlerMethod == null) {
            return model;
        }

        JavadocReader javadocReader = getJavadocReader(operation);
        SnippetTranslationResolver translationResolver = getTranslationResolver(operation);
        String methodComment = resolveComment(handlerMethod, javadocReader);
        String seeTagComment = resolveSeeTag(handlerMethod, javadocReader, translationResolver);
        String deprecatedComment = resolveDeprecated(handlerMethod, javadocReader, translationResolver);
        String completeComment = join("<p>", deprecatedComment, methodComment, seeTagComment);
        String description = convertFromJavadoc(completeComment,
                determineTemplateFormatting(operation));

        model.put("description", description);
        return model;
    }

    protected String resolveDeprecated(HandlerMethod handlerMethod, JavadocReader javadocReader, SnippetTranslationResolver translationResolver) {
        boolean isDeprecated = handlerMethod.getMethod().getAnnotation(Deprecated.class) != null;
        String deprecatedDoc = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "deprecated");
        if (isDeprecated || isNotBlank(deprecatedDoc)) {
            return addDot(translationResolver.translate("tags-deprecated", capitalize(deprecatedDoc)));
        } else {
            return "";
        }
    }

    protected String resolveComment(HandlerMethod handlerMethod, JavadocReader javadocReader) {
        String methodComment = javadocReader.resolveMethodComment(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName());
        return addDot(capitalize(methodComment));
    }

    private String resolveSeeTag(HandlerMethod handlerMethod, JavadocReader javadocReader, SnippetTranslationResolver translationResolver) {
        String comment = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "see");
        if (isNotBlank(comment)) {
            return addDot(translationResolver.translate("tags-see", comment));
        } else {
            return "";
        }
    }

    private Map<String, Object> defaultModel() {
        Map<String, Object> model = new HashMap<>();
        model.put("description", "");
        return model;
    }
}
