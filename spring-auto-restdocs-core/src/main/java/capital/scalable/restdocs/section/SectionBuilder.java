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
            SnippetRegistry.AUTHORIZATION,
            SnippetRegistry.PATH_PARAMETERS,
            SnippetRegistry.REQUEST_PARAMETERS,
            SnippetRegistry.REQUEST_FIELDS,
            SnippetRegistry.RESPONSE_FIELDS,
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
