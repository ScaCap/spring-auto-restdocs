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
package capital.scalable.restdocs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FieldUtilTest {
    @Test
    public void fromGetter() throws Exception {
        assertThat(FieldUtil.fromGetter("GetField"), is("GetField"));
        assertThat(FieldUtil.fromGetter("getField"), is("field"));
        assertThat(FieldUtil.fromGetter("IsField"), is("IsField"));
        assertThat(FieldUtil.fromGetter("isField"), is("field"));
    }

    @Test
    public void isGetter() throws Exception {
        assertThat(FieldUtil.isGetter("GetField"), is(false));
        assertThat(FieldUtil.isGetter("getField"), is(true));
        assertThat(FieldUtil.isGetter("IsField"), is(false));
        assertThat(FieldUtil.isGetter("isField"), is(true));
    }
}
