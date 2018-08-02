/*-
 * #%L
 * Spring Auto REST Docs Kotlin Web MVC Example Project
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
package capital.scalable.restdocs.example.items;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Java object for a single JSON item.
 * <p>
 * This class is intentionally kept in Java to demonstrate the support
 * for mixed Javadoc/KDoc in projects.
 */
class ItemResponse {
    /**
     * Unique ID. This text comes directly from Javadoc.
     */
    @NotBlank
    private String id;

    @JsonIgnore
    private String desc;

    /**
     * Metadata.
     * <p>
     * An example of JsonSubType support.
     *
     * @see <a href="https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations#type-handling">
     * Jackson type documentation</a>
     */
    private Metadata meta;

    /**
     * Item attributes.
     */
    private Attributes attributes;

    /**
     * Child items (recursive). Same structure as this response.
     */
    @Valid
    @NotEmpty
    private List<ItemResponse> children;

    /**
     * Tags.
     */
    private String[] tags;

    public ItemResponse(
            String id,
            String desc,
            Metadata meta,
            Attributes attributes,
            List<ItemResponse> children,
            String[] tags) {
        this.id = id;
        this.desc = desc;
        this.meta = meta;
        this.attributes = attributes;
        this.children = children;
        this.tags = tags;
    }

    /**
     * Some information | description about the item.
     */
    public String getDescription() {
        return desc;
    }
}
