package capital.scalable.restdocs.jackson.payload;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.restdocs.test.SnippetMatchers.TableMatcher;

public class TableWithPrefixMatcher extends BaseMatcher<String> {

    private final String prefix;

    private final TableMatcher<?> tableMatcher;

    private TableWithPrefixMatcher(String prefix, TableMatcher<?> tableMatcher) {
        this.prefix = prefix;
        this.tableMatcher = tableMatcher;
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof String) {
            String content = (String) item;
            return content.startsWith(prefix) &&
                    tableMatcher.matches(content.substring(prefix.length()));
        } else {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        tableMatcher.describeTo(description);
    }

    public static TableWithPrefixMatcher tableWithPrefix(String prefix,
            TableMatcher<?> tableMatcher) {
        return new TableWithPrefixMatcher(prefix, tableMatcher);
    }
}
