package capital.scalable.restdocs;

import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.section.SectionSupport;
import org.springframework.restdocs.operation.Operation;

public class SnippetRegistry {
    public static final String AUTHORIZATION = "auto-authorization";
    public static final String DESCRIPTION = "auto-description";
    public static final String METHOD_PATH = "auto-method-path";
    public static final String PATH_PARAMETERS = "auto-path-parameters";
    public static final String REQUEST_PARAMETERS = "auto-request-parameters";
    public static final String REQUEST_FIELDS = "auto-request-fields";
    public static final String RESPONSE_FIELDS = "auto-response-fields";
    public static final String CURL_REQUEST = "curl-request";
    public static final String HTTP_REQUEST = "http-request";
    public static final String HTTP_RESPONSE = "http-response";
    public static final String HTTPIE_REQUEST = "httpie-request";

    private static final Map<String, SectionSupport> CLASSIC_SNIPPETS;

    static {
        CLASSIC_SNIPPETS = new HashMap<>();
        CLASSIC_SNIPPETS.put(CURL_REQUEST, section(CURL_REQUEST, "Example request", true));
        CLASSIC_SNIPPETS.put(HTTPIE_REQUEST, section(HTTPIE_REQUEST, "Example request", true));
        CLASSIC_SNIPPETS.put(HTTP_REQUEST, section(HTTP_REQUEST, "Example request", true));
        CLASSIC_SNIPPETS.put(HTTP_RESPONSE, section(HTTP_RESPONSE, "Example response", true));
    }

    public static SectionSupport getClassicSnippet(String snippetName) {
        return CLASSIC_SNIPPETS.get(snippetName);
    }

    private static SectionSupport section(String snippetName, String header, boolean hasContent) {
        return new ClassicSnippetSection(snippetName, header, hasContent);
    }

    private static class ClassicSnippetSection implements SectionSupport {
        private String fileName;
        private String header;
        private boolean hasContent;

        public ClassicSnippetSection(String fileName, String header, boolean hasContent) {
            this.fileName = fileName;
            this.header = header;
            this.hasContent = hasContent;
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String getHeader() {
            return header;
        }

        @Override
        public boolean hasContent(Operation operation) {
            return hasContent;
        }
    }

    private SnippetRegistry() {
        // constants
    }
}
