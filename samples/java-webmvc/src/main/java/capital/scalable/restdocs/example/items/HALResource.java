/*-
 * #%L
 * Spring Auto REST Docs Java Web MVC Example Project
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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;

/**
 * Class to easily embedd relations.
 *
 * @see <a href="https://stackoverflow.com/a/25874652/410531">Stack Overflow</a>
 */
abstract class HALResource extends ResourceSupport {

    @JsonIgnore
    private List<EmbeddedWrapper> embedded = new ArrayList<>();

    @JsonIgnore
    private EmbeddedWrappers wrapper = new EmbeddedWrappers(false);

    @JsonInclude(NON_EMPTY)
    @JsonUnwrapped
    public Resources<EmbeddedWrapper> getEmbeddedResources() {
        return new Resources(embedded);
    }

    public void embed(String rel, Object resource) {
        if (resource != null) {
            embedded.add(wrapper.wrap(resource, rel));
        }
    }
}
