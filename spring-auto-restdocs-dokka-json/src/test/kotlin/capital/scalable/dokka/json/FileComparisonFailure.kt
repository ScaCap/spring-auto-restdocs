/*-
 * #%L
 * Spring Auto REST Docs Dokka JSON
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
package capital.scalable.dokka.json

import junit.framework.ComparisonFailure

class FileComparisonFailure(
    message: String,
    expected: String?,
    actual: String?,
    expectedFilePath: String
) : ComparisonFailure(message, expected, actual) {

    private val myExpected: String
    private val myActual: String
    private val filePath: String

    init {
        when {
            expected == null -> throw NullPointerException("'expected' must not be null")
            actual == null -> throw NullPointerException("'actual' must not be null")
            else -> {
                this.myExpected = expected
                this.myActual = actual
                this.filePath = expectedFilePath
            }
        }
    }

    override fun getExpected(): String {
        return this.myExpected
    }

    override fun getActual(): String {
        return this.myActual
    }
}
