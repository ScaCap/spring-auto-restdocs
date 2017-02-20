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

package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.misc.SnippetRegistry.AUTHORIZATION;
import static capital.scalable.restdocs.misc.SnippetRegistry.CURL_REQUEST;
import static capital.scalable.restdocs.misc.SnippetRegistry.HTTP_RESPONSE;
import static capital.scalable.restdocs.misc.SnippetRegistry.PATH_PARAMETERS;
import static capital.scalable.restdocs.misc.SnippetRegistry.REQUEST_FIELDS;
import static capital.scalable.restdocs.misc.SnippetRegistry.REQUEST_PARAMETERS;
import static capital.scalable.restdocs.misc.SnippetRegistry.RESPONSE_FIELDS;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SectionBuilder {

    public static final Collection<String> DEFAULT_SNIPPETS = Arrays.asList(
            AUTHORIZATION,
            PATH_PARAMETERS,
            REQUEST_PARAMETERS,
            REQUEST_FIELDS,
            RESPONSE_FIELDS,
            CURL_REQUEST,
            HTTP_RESPONSE
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
     */
    public SectionBuilder snippetNames(String... sectionNames) {
        this.snippetNames = Arrays.asList(sectionNames);
        return this;
    }

    /**
     * If snippet would not generate any output (e.g. endpoint has no path parameters),
     * than this snippet would not be included in the section.
     *
     * @param skipEmpty true if snippets with no content should not be included in the section;
     *                  false otherwise (default)
     */
    public SectionBuilder skipEmpty(boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
        return this;
    }

    public SectionSnippet build() {
        return new SectionSnippet(snippetNames, skipEmpty);
    }
}
