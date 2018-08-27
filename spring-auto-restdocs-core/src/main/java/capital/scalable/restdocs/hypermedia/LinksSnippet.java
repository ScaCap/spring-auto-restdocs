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
package capital.scalable.restdocs.hypermedia;

import static capital.scalable.restdocs.OperationAttributeHelper.getJavadocReader;
import static capital.scalable.restdocs.constraints.ConstraintReader.DEPRECATED_ATTRIBUTE;
import static capital.scalable.restdocs.constraints.ConstraintReader.OPTIONAL_ATTRIBUTE;
import static java.util.stream.Collectors.toList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import capital.scalable.restdocs.javadoc.JavadocReader;
import capital.scalable.restdocs.snippet.StandardTableSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.web.method.HandlerMethod;

public class LinksSnippet extends StandardTableSnippet {
    public static final String LINKS = "auto-links";

    public LinksSnippet() {
        super(LINKS, null);
    }

    @Override
    protected Collection<FieldDescriptor> createFieldDescriptors(Operation operation, HandlerMethod handlerMethod) {
        JavadocReader javadocReader = getJavadocReader(operation);
        Collection<String> links = javadocReader.resolveClassTags(handlerMethod.getBeanType(), "link");
        return links.stream()
                .map(this::parse)
                .collect(toList());
    }

    @Override
    protected String[] getTranslationKeys() {
        return new String[]{
                "th-rel",
                "th-optional",
                "th-description",
                "no-links"
        };
    }

    private FieldDescriptor parse(String link) {
        Map<String, String> map = toMap(link);
        String rel = map.get("rel");
        String comment = map.get("comment");
        String optional = map.get("optional");
        String deprecated = map.get("deprecated");

        FieldDescriptor fieldDescriptor = fieldWithPath(rel)
                .description(comment);

        Attribute optionalAttr = new Attribute(OPTIONAL_ATTRIBUTE, optional);
        Attribute deprecatedAttr = new Attribute(DEPRECATED_ATTRIBUTE, deprecated);
        fieldDescriptor.attributes(optionalAttr, deprecatedAttr);

        return fieldDescriptor;
    }

    private Map<String, String> toMap(String link) {
        Map<String, String> result = new HashMap<>();
        for (String part : link.split(";")) {
            String[] pair = part.split("=");
            if (pair.length == 2) {
                result.put(pair[0].trim(), pair[1].trim());
            }
        }
        return result;
    }
}
