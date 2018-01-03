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

import javax.validation.constraints.Size;

import capital.scalable.restdocs.example.constraints.OneOf;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Java object for the JSON request.
 */
@Data
class ItemUpdateRequest {
    /**
     * Some information about the item.
     */
    @NotBlank(groups = English.class)
    @Length(max = 20)
    @Size.List({
            @Size(min = 2, max = 10, groups = German.class),
            @Size(min = 4, max = 12, groups = English.class)
    })
    private String description;

    /**
     * Country dependent type of the item.
     */
    @Size(max = 1000)
    @OneOf.List({
            @OneOf(value = {"klein", "groß"}, groups = German.class),
            @OneOf(value = {"small", "big"}, groups = English.class)
    })
    private String type;
}
