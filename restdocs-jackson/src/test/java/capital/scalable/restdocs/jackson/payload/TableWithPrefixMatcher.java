package capital.scalable.restdocs.jackson.payload;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.test.SnippetMatchers.TableMatcher;

public class TableWithPrefixMatcher extends BaseMatcher<String> {

    private final TemplateFormat templateFormat;

    private final String asciiDocPrefix;

    private final String markdownDocPrefix;

    private final TableMatcher<?> tableMatcher;

    private TableWithPrefixMatcher(TemplateFormat templateFormat, String asciiDocPrefix,
            String markdownDocPrefix, TableMatcher<?> tableMatcher) {
        this.templateFormat = templateFormat;
        this.asciiDocPrefix = asciiDocPrefix;
        this.markdownDocPrefix = markdownDocPrefix;
        this.tableMatcher = tableMatcher;
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof String) {
            String content = (String) item;
            String prefix = prefix();
            return content.startsWith(prefix) &&
                    tableMatcher.matches(content.substring(prefix.length()));
        } else {
            return false;
        }
    }

    private String prefix() {
        if ("adoc".equals(templateFormat.getFileExtension())) {
            return asciiDocPrefix;
        } else {
            return markdownDocPrefix;
        }
    }

    @Override
    public void describeTo(Description description) {
        tableMatcher.describeTo(description);
    }

    public static TableWithPrefixMatcher tableWithPrefix(TemplateFormat templateFormat,
            String asciiDocPrefix, String markdownDocPrefix, TableMatcher<?> tableMatcher) {
        return new TableWithPrefixMatcher(templateFormat, asciiDocPrefix, markdownDocPrefix,
                tableMatcher);
    }
}
