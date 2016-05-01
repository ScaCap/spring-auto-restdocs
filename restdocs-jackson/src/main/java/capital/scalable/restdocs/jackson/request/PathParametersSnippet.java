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

package capital.scalable.restdocs.jackson.request;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Juraj Misur
 */
public class PathParametersSnippet extends AbstractParameterSnippet<PathVariable> {

    public PathParametersSnippet() {
        super("path-parameters", null);
    }

    @Override
    protected boolean isRequired(PathVariable annot) {
        return true;
    }

    @Override
    protected String getPath(PathVariable annot) {
        return annot.value();
    }

    protected PathVariable getAnnotation(MethodParameter param) {
        return param.getParameterAnnotation(PathVariable.class);
    }
}
