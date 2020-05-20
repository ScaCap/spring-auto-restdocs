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

import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

public class HandlerMethodUtil {
    private HandlerMethodUtil() {
        // util
    }
    public static boolean isPageRequest(HandlerMethod method) {
        for (MethodParameter param : method.getMethodParameters()) {
            if (isPageable(param)) {
                return true;
            }
        }
        return false;
    }

    public static final String SPRING_DATA_PAGEABLE_CLASS =
            "org.springframework.data.domain.Pageable";
    private static boolean isPageable(MethodParameter param) {
        return SPRING_DATA_PAGEABLE_CLASS.equals(
                param.getParameterType().getCanonicalName());
    }

}
