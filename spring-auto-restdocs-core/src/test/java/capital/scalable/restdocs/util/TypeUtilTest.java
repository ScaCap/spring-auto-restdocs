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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class TypeUtilTest {
    @Test
    public void determineTypeName() throws Exception {
        assertThat(TypeUtil.determineTypeName(byte.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(Byte.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(short.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(Short.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(long.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(Long.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(int.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(Integer.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(BigInteger.class)).isEqualTo("Integer");
        assertThat(TypeUtil.determineTypeName(float.class)).isEqualTo("Decimal");
        assertThat(TypeUtil.determineTypeName(Float.class)).isEqualTo("Decimal");
        assertThat(TypeUtil.determineTypeName(double.class)).isEqualTo("Decimal");
        assertThat(TypeUtil.determineTypeName(Double.class)).isEqualTo("Decimal");
        assertThat(TypeUtil.determineTypeName(BigDecimal.class)).isEqualTo("Decimal");
        assertThat(TypeUtil.determineTypeName(char.class)).isEqualTo("String");
        assertThat(TypeUtil.determineTypeName(Character.class)).isEqualTo("String");
        assertThat(TypeUtil.determineTypeName(boolean.class)).isEqualTo("Boolean");
        assertThat(TypeUtil.determineTypeName(Boolean.class)).isEqualTo("Boolean");
    }

    @Test
    public void isPrimitive() throws Exception {
        assertThat(TypeUtil.isPrimitive(TestClass.class, "primitive")).isTrue();
        assertThat(TypeUtil.isPrimitive(TestClass.class, "wrapper")).isFalse();
    }

    static class TestClass {
        private boolean primitive;
        private Boolean wrapper;
    }
}
