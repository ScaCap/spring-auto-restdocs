/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.jsondoclet;

import java.math.BigDecimal;

/**
 * This is a test class. UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép
 *
 * @ Don't fail on invalid tags
 */
class DocumentedClass {
    /**
     * Location of resource
     *
     * @see path
     */
    public String location;

    /**
     * Path within location
     *
     * @ Don't fail on invalid tags
     */
    private BigDecimal path;

    Boolean notDocumented;

    /**
     * Executes request on specified location
     *
     * @title Execute request
     */
    public void execute() {
    }

    /**
     * Initiates request (テスト)
     *
     * @param when  when to initiate (テスト)
     * @param force true if force (テスト)
     * @title Do initiate (テスト)
     * @ Don't fail on invalid tags
     * @deprecated use other method (テスト)
     */
    private void initiate(String when, boolean force, int notDocumented) {
    }

    void notDocumented() {
    }

    /**
     * @title Title tag
     */
    void documentedWithOnlyTag() {
    }
}
