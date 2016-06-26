package capital.scalable.restdocs.jackson.payload;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.restdocs.test.SnippetMatchers;

public class TableWithPrefixMatcher extends BaseMatcher<String> {

    private final String prefix;

    private final SnippetMatchers.TableMatcher<?> tableMatcher;

    private TableWithPrefixMatcher(String prefix, SnippetMatchers.TableMatcher<?> tableMatcher) {
        this.prefix = prefix;
        this.tableMatcher = tableMatcher;
    }

    public boolean matches(Object item) {
        if (item instanceof String) {
            String content = (String) item;
            System.out.println(content.substring(prefix.length()));
            return content.startsWith(prefix) &&
                    tableMatcher.matches(content.substring(prefix.length()));
        } else {
            return false;
        }
    }

    public void describeTo(Description description) {
        tableMatcher.describeTo(description);
    }

    public static TableWithPrefixMatcher tableWithPrefix(String prefix,
            SnippetMatchers.TableMatcher<?> tableMatcher) {
        return new TableWithPrefixMatcher(prefix, tableMatcher);
    }
}
