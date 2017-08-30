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
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.springframework.util.StringUtils.capitalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.javadoc.JavadocReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolverFactory;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.method.HandlerMethod;

public class SectionSnippet extends TemplatedSnippet {

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
        HandlerMethod handlerMethod = getHandlerMethod(operation);
        JavadocReader javadocReader = getJavadocReader(operation);

        String title = resolveTitle(handlerMethod, javadocReader);

        // resolve path
        String path = propertyPlaceholderHelper.replacePlaceholders(operation.getName(),
                placeholderResolverFactory.create(getDocumentationContext(operation)));

        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("link", delimit(path));
        model.put("path", path);
        List<SectionSupport> sections = new ArrayList<>();
        model.put("sections", sections);

        for (String sectionName : sectionNames) {
            SectionSupport section = getSectionSnippet(operation, sectionName);
            if (section != null) {
                if (!skipEmpty || section.hasContent(operation)) {
                    sections.add(section);
                }
            } else {
                System.out.println("Section snippet '" + sectionName + "' is configured to be " +
                        "included in the section but no such snippet is present in configuration");
            }
        }

        return model;
    }

    private String resolveTitle(HandlerMethod handlerMethod, JavadocReader javadocReader) {
        String title = javadocReader.resolveMethodTitle(handlerMethod.getBeanType(),
                handlerMethod.getMethod().getName());
        if (StringUtils.isBlank(title)) {
            title = join(splitByCharacterTypeCamelCase(
                    capitalize(handlerMethod.getMethod().getName())), ' ');
        }
        return title;
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
}
