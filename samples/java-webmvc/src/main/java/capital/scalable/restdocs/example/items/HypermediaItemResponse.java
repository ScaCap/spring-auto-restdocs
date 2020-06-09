/*-
 * #%L
 * Spring Auto REST Docs Java Web MVC Example Project
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
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

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capital.scalable.restdocs.jackson.RestdocsNotExpanded;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalLinkRelation;

@JsonPropertyOrder({"id", "desc"})
class HypermediaItemResponse extends RepresentationModel<HypermediaItemResponse> {
    /**
     * Item id.
     */
    @JsonProperty
    private String id;

    /**
     * Item description.
     */
    @JsonProperty
    private String desc;

    @JsonProperty("_embedded") //
    @JsonInclude(JsonInclude.Include.NON_EMPTY) //
    private Map<HalLinkRelation, Object> embedded = new HashMap<>();

    HypermediaItemResponse(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public void addEmbedded(String linkRel, Object data) {
        embedded.put(HalLinkRelation.uncuried(linkRel), data);
    }

    // just for documentation
    static class ExtendedLinksDocumentation {
        /**
         * Link to this hypermedia item.
         */
        @NotNull
        private String self;

        /**
         * Link to classic item.
         *
         * @deprecated Use hypermedia item instead.
         */
        @Deprecated
        @NotNull
        private String classicItem;

        /**
         * Link to initiate processing of the current item. Present only if item is processable.
         */
        private String process;
    }

    // just for documentation
    static class EmbeddedDocumentation {
        /**
         * Item's children.
         */
        @RestdocsNotExpanded
        private List<ItemResponse> children;

        /**
         * Item's attributes.
         */
        @RestdocsNotExpanded
        private Attributes attributes;

        /**
         * Item's meta.
         */
        @RestdocsNotExpanded
        private Metadata meta;
    }
}
