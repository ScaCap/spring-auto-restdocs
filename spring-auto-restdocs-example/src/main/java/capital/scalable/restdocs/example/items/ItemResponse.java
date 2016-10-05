/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.example.items;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import capital.scalable.restdocs.jackson.RestdocsNotExpanded;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Java object for a single JSON item.
 */
@AllArgsConstructor
class ItemResponse {
    /**
     * Unique ID. This text comes directly from JavaDoc.
     */
    @NotBlank
    private String id;

    @JsonIgnore
    private String desc;

    /**
     * Metadata.
     */
    @JsonUnwrapped
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
    @RestdocsNotExpanded
    private List<ItemResponse> children;

    /**
     * Some information about the item.
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
    }

    @AllArgsConstructor
    static class Metadata {
        /**
         * Custom meta attribute 1
         */
        private String custom1;
        /**
         * Custom meta attribute 2
         */
        private Integer custom2;
    }
}
