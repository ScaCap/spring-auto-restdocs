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
package capital.scalable.restdocs.section;

import static capital.scalable.restdocs.OperationAttributeHelper.getDefaultSnippets;
import static capital.scalable.restdocs.OperationAttributeHelper.getDocumentationContext;
import static capital.scalable.restdocs.OperationAttributeHelper.getHandlerMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.OperationAttributeHelper.getTranslationResolver;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
import java.util.Optional;

import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.slf4j.Logger;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolverFactory;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippet extends TemplatedSnippet {
    private static final Logger log = getLogger(SectionSnippet.class);

    public static final String SECTION = "auto-section";

    protected final RestDocumentationContextPlaceholderResolverFactory placeholderResolverFactory =
            new RestDocumentationContextPlaceholderResolverFactory();

    protected final PropertyPlaceholderHelper propertyPlaceholderHelper =
            new PropertyPlaceholderHelper("{", "}");

    protected final Collection<String> sectionNames;
    protected final boolean skipEmpty;

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
        SnippetTranslationResolver translationResolver = getTranslationResolver(operation);
        String title = resolveTitle(handlerMethod, javadocReader, translationResolver);

        model.put("title", title);
        model.put("sections", createSections(operation, translationResolver));

        return model;
    }

    protected Collection<Section> createSections(Operation operation, SnippetTranslationResolver translationResolver) {
        List<Section> sections = new ArrayList<>();
        for (String sectionName : sectionNames) {
            SectionSupport section = getSectionSnippet(operation, sectionName);
            if (section != null) {
                boolean hasContent = section.hasContent(operation);
                if (!skipEmpty || hasContent) {
                    String header = translationResolver.translate(section.getHeaderKey(operation));
                    Optional<Section> toAdd = sections.stream().filter(s -> s.header.equals(header)).findFirst();
                    if (section.isMergeable() && toAdd.isPresent()) {
                        toAdd.get().addSection(section.getFileName(), hasContent);
                    } else {
                        sections.add(new Section(header, section.getFileName(), hasContent));
                    }
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

    protected String resolveTitle(HandlerMethod handlerMethod, JavadocReader javadocReader,
            SnippetTranslationResolver translationResolver) {
        String title = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "title");
        if (isBlank(title)) {
            title = createTitle(handlerMethod.getMethod().getName());
        }
        boolean isDeprecated = handlerMethod.getMethod().getAnnotation(Deprecated.class) != null;
        String deprecated = javadocReader.resolveMethodTag(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName(), "deprecated");
        if (isDeprecated || isNotBlank(deprecated)) {
            return translationResolver.translate("tags-deprecated-title", title);
        } else {
            return title;
        }
    }

    private String createTitle(String name) {
        return join(splitByCharacterTypeCamelCase(capitalize(name)), ' ');
    }

    private SectionSupport getSectionSnippet(Operation operation, String snippetName) {
        return getDefaultSnippets(operation).stream()
                .filter(snippet -> snippet instanceof SectionSupport)
                .map(snippet -> (SectionSupport) snippet)
                .filter(snippet -> snippetName.equals(snippet.getFileName()))
                .findFirst()
                .orElseGet(() -> SnippetRegistry.getClassicSnippet(snippetName));
    }

    private String delimit(String value) {
        return value.replace("/", "-");
    }

    static class Section {
        String header;
        List<String> filenames;
        boolean hasContent;

        public Section(String header, String filename, boolean hasContent) {
            this.header = header;
            this.filenames = new ArrayList<>(singletonList(filename));
            this.hasContent = hasContent;
        }

        void addSection(String filename, boolean hasContent) {
            if (!hasContent) {
                return;
            }
            if (!this.hasContent) {
                this.hasContent = hasContent;
                this.filenames = new ArrayList<>(singletonList(filename));
            } else {
                this.hasContent = hasContent;
                filenames.add(filename);
            }
        }
    }
}
