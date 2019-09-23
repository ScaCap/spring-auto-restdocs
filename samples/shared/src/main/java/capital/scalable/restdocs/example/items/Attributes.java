/*-
 * #%L
 * Spring Auto REST Docs Shared POJOs Example Project
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import capital.scalable.restdocs.example.common.Money;

/**
 * Various attributes about the item.
 */
public class Attributes {
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

    Attributes(String text, Integer number, Boolean bool, BigDecimal decimal, Money amount, EnumType enumType) {
        this.text = text;
        this.number = number;
        this.bool = bool;
        this.decimal = decimal;
        this.amount = amount;
        this.enumType = enumType;
    }
}
