/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.section;

import org.springframework.restdocs.operation.Operation;

public interface SectionSupport {
    /**
     * File name (= section name).
     */
    String getFileName();

    /**
     * Section header key corresponding to entry in translation file.
     *
     * @param operation operation
     */
    String getHeaderKey(Operation operation);

    /**
     * Flag if section will render non-empty content.
     *
     * @param operation operation
     * @return true if snippet will return non-empty content; false otherwise
     */
    boolean hasContent(Operation operation);

    /**
     * Flag if section is mergeable with other sections with the same header
     *
     * @return true if this section is mergeable; false otherwise
     */
    default boolean isMergeable() {
        return false;
    }
}
