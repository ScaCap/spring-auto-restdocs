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

package capital.scalable.example.items;

import javax.validation.constraints.Size;

import capital.scalable.example.constraints.OneOf;
import lombok.Data;
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
    @Size.List({
            @Size(min = 2, max = 10, groups = German.class),
            @Size(min = 4, max = 12, groups = English.class)
    })
    private String description;

    /**
     * Country dependent type of the item.
     */
    @OneOf.List({
            @OneOf(value = {"klein", "groß"}, groups = German.class),
            @OneOf(value = {"small", "big"}, groups = English.class)
    })
    private String type;
}
