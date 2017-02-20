package capital.scalable.restdocs.misc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;

public class SnippetRegistry {
    public static final String AUTHORIZATION = "authorization";
    public static final String DESCRIPTION = "description";
    public static final String METHOD_PATH = "method-path";
    public static final String PATH_PARAMETERS = "path-parameters";
    public static final String REQUEST_PARAMETERS = "request-parameters";
    public static final String REQUEST_FIELDS = "request-fields";
    public static final String RESPONSE_FIELDS = "response-fields";
    public static final String CURL_REQUEST = "curl-request";
    public static final String HTTP_REQUEST = "http-request";
    public static final String HTTP_RESPONSE = "http-response";
    public static final String HTTPIE_REQUEST = "httpie-request";

    public static final Map<String, SectionSupport> CLASSIC_SNIPPETS;

    static {
        CLASSIC_SNIPPETS = new HashMap<>();
        CLASSIC_SNIPPETS.put(CURL_REQUEST, section(CURL_REQUEST, "Example request", true));
        CLASSIC_SNIPPETS.put(HTTPIE_REQUEST, section(HTTPIE_REQUEST, "Example request", true));
        CLASSIC_SNIPPETS.put(HTTP_REQUEST, section(HTTP_REQUEST, "Example request", true));
        CLASSIC_SNIPPETS.put(HTTP_RESPONSE, section(HTTP_RESPONSE, "Example response", true));
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
