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
package capital.scalable.restdocs.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FieldUtilTest {
    @Test
    public void fromGetter() throws Exception {
        assertThat(FieldUtil.fromGetter("GetField")).isEqualTo("GetField");
        assertThat(FieldUtil.fromGetter("getField")).isEqualTo("field");
        assertThat(FieldUtil.fromGetter("IsField")).isEqualTo("IsField");
        assertThat(FieldUtil.fromGetter("isField")).isEqualTo("field");
    }

    @Test
    public void isGetter() throws Exception {
        assertThat(FieldUtil.isGetter("GetField")).isFalse();
        assertThat(FieldUtil.isGetter("getField")).isTrue();
        assertThat(FieldUtil.isGetter("IsField")).isFalse();
        assertThat(FieldUtil.isGetter("isField")).isTrue();
    }
}
