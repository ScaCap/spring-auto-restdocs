/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.payload;

import org.assertj.core.api.Condition;
import org.springframework.restdocs.testfixtures.SnippetConditions.TableCondition;

public class TableWithPrefixMatcher extends Condition<String> {

    private final String prefix;

    private final TableCondition<?> tableMatcher;

    private TableWithPrefixMatcher(String prefix, TableCondition<?> tableMatcher) {
        this.prefix = prefix;
        this.tableMatcher = tableMatcher;
    }

    @Override
    public boolean matches(String content) {
        return content.startsWith(prefix) && tableMatcher.matches(content.substring(prefix.length()));
    }

    public static TableWithPrefixMatcher tableWithPrefix(String prefix,
            TableCondition<?> tableMatcher) {
        return new TableWithPrefixMatcher(prefix, tableMatcher);
    }
}
