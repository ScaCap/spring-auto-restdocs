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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import capital.scalable.restdocs.SnippetRegistry;

/**
 * Builder for configuring a section snippet.
 */
public class SectionBuilder {

    public static final Collection<String> DEFAULT_SNIPPETS = Arrays.asList(
            SnippetRegistry.AUTO_AUTHORIZATION,
            SnippetRegistry.AUTO_PATH_PARAMETERS,
            SnippetRegistry.AUTO_REQUEST_PARAMETERS,
            SnippetRegistry.AUTO_MODELATTRIBUTE,
            SnippetRegistry.AUTO_REQUEST_FIELDS,
            SnippetRegistry.AUTO_RESPONSE_FIELDS,
            SnippetRegistry.AUTO_LINKS,
            SnippetRegistry.AUTO_EMBEDDED,
            SnippetRegistry.CURL_REQUEST,
            SnippetRegistry.HTTP_RESPONSE
    );

    private Collection<String> snippetNames = DEFAULT_SNIPPETS;
    private boolean skipEmpty = false;

    public SectionBuilder() {
    }

    /**
     * List of snippet names to use in the specified order.
     *
     * @param snippetNames snippet names to use; if not specified, {@link #DEFAULT_SNIPPETS} will
     *                     be used
     * @return this
     */
    public SectionBuilder snippetNames(List<String> snippetNames) {
        this.snippetNames = snippetNames;
        return this;
    }

    /**
     * Array of snippet names to use in the specified order.
     *
     * @param snippetNames snippet names to use; if not specified, {@link #DEFAULT_SNIPPETS} will
     *                     be used
     * @return this
     */
    public SectionBuilder snippetNames(String... snippetNames) {
        this.snippetNames = Arrays.asList(snippetNames);
        return this;
    }

    /**
     * If snippet would not generate any output (e.g. endpoint has no path parameters),
     * than this snippet would not be included in the section.
     *
     * @param skipEmpty true if snippets with no content should not be included in the section;
     *                  false otherwise (default)
     * @return this
     */
    public SectionBuilder skipEmpty(boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
        return this;
    }

    public SectionSnippet build() {
        return new SectionSnippet(snippetNames, skipEmpty);
    }
}
