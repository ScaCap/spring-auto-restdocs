/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.hypermedia;

import static capital.scalable.restdocs.SnippetRegistry.AUTO_EMBEDDED;

import java.lang.reflect.Type;

import capital.scalable.restdocs.payload.AbstractJacksonFieldSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.method.HandlerMethod;

public class EmbeddedSnippet extends AbstractJacksonFieldSnippet {

    private final Type documentationType;
    private final boolean failOnUndocumentedFields;

    public EmbeddedSnippet() {
        this(null, false);
    }

    public EmbeddedSnippet(Type documentationType, boolean failOnUndocumentedFields) {
        super(AUTO_EMBEDDED, null);
        this.documentationType = documentationType;
        this.failOnUndocumentedFields = failOnUndocumentedFields;
    }

    public EmbeddedSnippet documentationType(Type documentationType) {
        return new EmbeddedSnippet(documentationType, failOnUndocumentedFields);
    }

    public EmbeddedSnippet failOnUndocumentedFields(boolean failOnUndocumentedFields) {
        return new EmbeddedSnippet(documentationType, failOnUndocumentedFields);
    }

    @Override
    protected Type[] getType(HandlerMethod method) {
        return documentationType == null ? null : new Type[]{documentationType};
    }

    @Override
    public String getHeaderKey(Operation operation) {
        return "embedded";
    }

    @Override
    protected boolean shouldFailOnUndocumentedFields() {
        return failOnUndocumentedFields;
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-path",
                "th-type",
                "th-optional",
                "th-description",
                "no-embedded"
        };
    }
}
