/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.section;

import static capital.scalable.restdocs.OperationAttributeHelper.getDefaultSnippets;
import static capital.scalable.restdocs.OperationAttributeHelper.getDocumentationContext;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.i18n.SnippetTranslationResolver.translate;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.capitalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.slf4j.Logger;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolverFactory;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippet extends TemplatedSnippet {
    private static final Logger log = getLogger(SectionSnippet.class);

    public static final String SECTION = "auto-section";

    private final RestDocumentationContextPlaceholderResolverFactory placeholderResolverFactory =
            new RestDocumentationContextPlaceholderResolverFactory();

    private final PropertyPlaceholderHelper propertyPlaceholderHelper =
            new PropertyPlaceholderHelper("{", "}");

    private final Collection<String> sectionNames;
    private final boolean skipEmpty;

    public SectionSnippet(Collection<String> sectionNames, boolean skipEmpty) {
        super(SECTION, null);
        this.sectionNames = sectionNames;
        this.skipEmpty = skipEmpty;
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = defaultModel(operation);

        HandlerMethod handlerMethod = getHandlerMethod(operation);
        if (handlerMethod == null) {
            return model;
        }

        JavadocReader javadocReader = getJavadocReader(operation);
        String title = resolveTitle(handlerMethod, javadocReader);

        model.put("title", title);
        model.put("sections", createSections(operation));

        createSections(operation);

        return model;
    }

    private List<Section> createSections(Operation operation) {
        List<Section> sections = new ArrayList<>();
        for (String sectionName : sectionNames) {
            SectionSupport section = getSectionSnippet(operation, sectionName);
            if (section != null) {
                if (!skipEmpty || section.hasContent(operation)) {
                    sections.add(
                            new Section(section.getFileName(), translate(section.getHeaderKey())));
                }
            } else {
                log.warn("Section snippet '" + sectionName + "' is configured to be " +
                        "included in the section but no such snippet is present in configuration");
            }
        }
        return sections;
    }

    private Map<String, Object> defaultModel(Operation operation) {
        // resolve path
        String path = propertyPlaceholderHelper.replacePlaceholders(operation.getName(),
                placeholderResolverFactory.create(getDocumentationContext(operation)));

        Map<String, Object> model = new HashMap<>();
        model.put("title", createTitle(operation.getName()));
        model.put("link", delimit(path));
        model.put("path", path);
        model.put("sections", emptyList());
        return model;
    }

    private String resolveTitle(HandlerMethod handlerMethod, JavadocReader javadocReader) {
        String title = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "title");
        if (isBlank(title)) {
            title = createTitle(handlerMethod.getMethod().getName());
        }
        boolean isDeprecated = handlerMethod.getMethod().getAnnotation(Deprecated.class) != null;
        String deprecated = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "deprecated");
        if (isDeprecated || isNotBlank(deprecated)) {
            return translate("tags-deprecated-title", title);
        } else {
            return title;
        }
    }

    private String createTitle(String name) {
        return join(splitByCharacterTypeCamelCase(capitalize(name)), ' ');
    }

    private SectionSupport getSectionSnippet(Operation operation, String snippetName) {
        for (Snippet snippet : getDefaultSnippets(operation)) {
            if (snippet instanceof SectionSupport) {
                SectionSupport sectionSnippet = (SectionSupport) snippet;
                if (snippetName.equals(sectionSnippet.getFileName())) {
                    return sectionSnippet;
                }
            }
        }

        return SnippetRegistry.getClassicSnippet(snippetName);
    }

    private String delimit(String value) {
        return value.replace("/", "-");
    }

    static class Section {
        String fileName;
        String header;

        public Section(String fileName, String header) {
            this.fileName = fileName;
            this.header = header;
        }
    }
}
