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

package capital.scalable.jsondoclet;

import java.math.BigDecimal;

/**
 * This is a test class
 */
public class DocumentedClass {
    /**
     * Location of resource
     */
    public String location;

    /**
     * Path within location
     */
    private BigDecimal path;

    Boolean notDocumented;

    /**
     * Executes request on specified location
     */
    public void execute() {
    }

    /**
     * Initiates request
     *
     * @param when  when to initiate
     * @param force true if force
     */
    private void initiate(String when, boolean force, int notDocumented) {
    }

    void notDocumented() {
    }
}
