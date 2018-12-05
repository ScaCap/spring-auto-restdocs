/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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
package capital.scalable.restdocs;

import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.section.SectionSupport;
import org.springframework.restdocs.operation.Operation;

public class SnippetRegistry {
    // Spring Auto REST Docs snippets
    public static final String AUTO_AUTHORIZATION = "auto-authorization";
    public static final String AUTO_DESCRIPTION = "auto-description";
    public static final String AUTO_METHOD_PATH = "auto-method-path";
    public static final String AUTO_PATH_PARAMETERS = "auto-path-parameters";
    public static final String AUTO_REQUEST_HEADERS = "auto-request-headers";
    public static final String AUTO_REQUEST_PARAMETERS = "auto-request-parameters";
    public static final String AUTO_REQUEST_FIELDS = "auto-request-fields";
    public static final String AUTO_RESPONSE_FIELDS = "auto-response-fields";
    public static final String AUTO_LINKS = "auto-links";
    public static final String AUTO_EMBEDDED = "auto-embedded";

    // Spring REST Docs classic snippets
    public static final String CURL_REQUEST = "curl-request";
    public static final String HTTP_REQUEST = "http-request";
    public static final String HTTP_RESPONSE = "http-response";
    public static final String HTTPIE_REQUEST = "httpie-request";
    public static final String PATH_PARAMETERS = "path-parameters";
    public static final String REQUEST_PARAMETERS = "request-parameters";
    public static final String REQUEST_FIELDS = "request-fields";
    public static final String RESPONSE_FIELDS = "response-fields";
    public static final String REQUEST_BODY_SNIPPET = "request-body";
    public static final String RESPONSE_BODY = "response-body";
    public static final String REQUEST_HEADERS = "request-headers";
    public static final String RESPONSE_HEADERS = "response-headers";
    public static final String REQUEST_PARTS = "request-parts";
    public static final String REQUEST_PART_FIELDS = "request-part-fields";
    public static final String LINKS = "links";

    private static final Map<String, SectionSupport> CLASSIC_SNIPPETS;

    static {
        CLASSIC_SNIPPETS = new HashMap<>();
        CLASSIC_SNIPPETS.put(CURL_REQUEST, section(CURL_REQUEST, "example-request", true));
        CLASSIC_SNIPPETS.put(HTTPIE_REQUEST, section(HTTPIE_REQUEST, "example-request", true));
        CLASSIC_SNIPPETS.put(HTTP_REQUEST, section(HTTP_REQUEST, "example-request", true));
        CLASSIC_SNIPPETS.put(HTTP_RESPONSE, section(HTTP_RESPONSE, "example-response", true));
        CLASSIC_SNIPPETS.put(REQUEST_PARAMETERS, section(REQUEST_PARAMETERS, "request-parameters", true));
        CLASSIC_SNIPPETS.put(PATH_PARAMETERS, section(PATH_PARAMETERS, "path-parameters", true));
        CLASSIC_SNIPPETS.put(LINKS, section(LINKS, "hypermedia-links", true));
        CLASSIC_SNIPPETS.put(REQUEST_FIELDS, section(REQUEST_FIELDS, "request-fields", true));
        CLASSIC_SNIPPETS.put(RESPONSE_FIELDS, section(RESPONSE_FIELDS, "response-fields", true));
        CLASSIC_SNIPPETS.put(REQUEST_BODY_SNIPPET, section(REQUEST_BODY_SNIPPET, "request-body", true));
        CLASSIC_SNIPPETS.put(RESPONSE_BODY, section(RESPONSE_BODY, "response-body", true));
        CLASSIC_SNIPPETS.put(REQUEST_HEADERS, section(REQUEST_HEADERS, "request-headers", true));
        CLASSIC_SNIPPETS.put(RESPONSE_HEADERS, section(RESPONSE_HEADERS, "response-headers", true));
        CLASSIC_SNIPPETS.put(REQUEST_PARTS, section(REQUEST_PARTS, "request-parts", true));
        CLASSIC_SNIPPETS.put(REQUEST_PART_FIELDS, section(REQUEST_PART_FIELDS, "request-part-fields", true));
    }

    public static SectionSupport getClassicSnippet(String snippetName) {
        return CLASSIC_SNIPPETS.get(snippetName);
    }

    private static SectionSupport section(String snippetName, String header, boolean hasContent) {
        return new ClassicSnippetSection(snippetName, header, hasContent);
    }

    private static class ClassicSnippetSection implements SectionSupport {
        private String fileName;
        private String headerKey;
        private boolean hasContent;

        public ClassicSnippetSection(String fileName, String headerKey, boolean hasContent) {
            this.fileName = fileName;
            this.headerKey = headerKey;
            this.hasContent = hasContent;
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String getHeaderKey() {
            return headerKey;
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
