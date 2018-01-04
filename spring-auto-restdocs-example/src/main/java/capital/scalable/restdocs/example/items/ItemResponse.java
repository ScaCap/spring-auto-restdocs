/*-
 * #%L
 * Spring Auto REST Docs Example Project
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

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Java object for a single JSON item.
 */
@AllArgsConstructor
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
     * @see
     * <a href="https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations#type-handling">
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

    /**
     * Some information | description about the item.
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Various attributes about the item.
     */
    @Value
    public static class Attributes {
        /**
         * Textual attribute.
         */
        @NotBlank
        @Size(min = 2, max = 20)
        private String text;
        /**
         * Integer attribute.
         */
        @NotNull
        @Min(1)
        @Max(10)
        private Integer number;
        /**
         * Boolean attribute.
         */
        private Boolean bool;
        /**
         * Decimal attribute.
         */
        @DecimalMin("1")
        @DecimalMax("10")
        private BigDecimal decimal;
        /**
         * Amount attribute.
         */
        @NotNull
        private Money amount;
        /**
         * Enum attribute.
         */
        @NotNull
        private EnumType enumType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @JsonTypeInfo(use = NAME, include = PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Metadata1.class, name = "1"),
            @JsonSubTypes.Type(value = Metadata2.class, name = "2")
    })
    static class Metadata {
        /**
         * Determines the type of metadata
         */
        @NotBlank
        private String type;
    }

    @NoArgsConstructor
    static class Metadata1 extends Metadata {
        /**
         * Tag attribute. Available only if metadata type=1
         */
        private String tag;

        Metadata1(String type, String tag) {
            super(type);
            this.tag = tag;
        }
    }

    static class Metadata2 extends Metadata {
        /**
         * Order attribute. Available only if metadata type=2
         */
        private Integer order;

        Metadata2(String type, Integer order) {
            super(type);
            this.order = order;
        }
    }

    enum EnumType {
        ONE, TWO
    }
}
