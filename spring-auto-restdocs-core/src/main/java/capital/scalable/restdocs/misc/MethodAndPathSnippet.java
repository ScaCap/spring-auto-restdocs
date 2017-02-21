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

package capital.scalable.restdocs.misc;

import static capital.scalable.restdocs.OperationAttributeHelper.getRequestMethod;
import static capital.scalable.restdocs.OperationAttributeHelper.getRequestPattern;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class MethodAndPathSnippet extends TemplatedSnippet {

    public static final String METHOD_PATH = "method-path";

    public MethodAndPathSnippet() {
        super(METHOD_PATH, null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("method", getRequestMethod(operation));
        model.put("path", nullToEmpty(getRequestPattern(operation)));
        return model;
    }

    private String nullToEmpty(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}