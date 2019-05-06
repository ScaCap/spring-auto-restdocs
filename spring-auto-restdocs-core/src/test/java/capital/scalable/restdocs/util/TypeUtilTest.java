/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class TypeUtilTest {
    @Test
    public void determineTypeName() throws Exception {
        assertThat(TypeUtil.determineTypeName(byte.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Byte.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(short.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Short.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(long.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Long.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(int.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(Integer.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(BigInteger.class), is("Integer"));
        assertThat(TypeUtil.determineTypeName(float.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(Float.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(double.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(Double.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(BigDecimal.class), is("Decimal"));
        assertThat(TypeUtil.determineTypeName(char.class), is("String"));
        assertThat(TypeUtil.determineTypeName(Character.class), is("String"));
        assertThat(TypeUtil.determineTypeName(boolean.class), is("Boolean"));
        assertThat(TypeUtil.determineTypeName(Boolean.class), is("Boolean"));
    }

    @Test
    public void isPrimitive() throws Exception {
        assertThat(TypeUtil.isPrimitive(TestClass.class, "primitive"), is(true));
        assertThat(TypeUtil.isPrimitive(TestClass.class, "wrapper"), is(false));
    }

    static class TestClass {
        private boolean primitive;
        private Boolean wrapper;
    }
}
