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


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

public class PathParametersSnippetTest extends AbstractSnippetTests {

    private ObjectMapper mapper = new ObjectMapper();

    public PathParametersSnippetTest(String name, TemplateFormat templateFormat) {
        super(name, templateFormat);
    }

    @Test
    public void simpleRequest() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(new TestResource(),
                "addItem", Integer.class, String.class);
        initParameters(handlerMethod);

        this.snippet.expectPathParameters("map-path").withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("id", "Integer", "false", "")
                        .row("subid", "String", "false", ""));

        new PathParametersSnippet().document(operationBuilder("map-path")
                .attribute(HandlerMethod.class.getName(), handlerMethod)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost/items/123/subitem/myItem")
                .build());
    }

    private void initParameters(HandlerMethod handlerMethod) {
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        }
    }

    private static class TestResource {

        @RequestMapping(value = "/items/{id}/subitem/{subid}")
        public void addItem(@PathVariable Integer id, @PathVariable("subid") String otherId) {
            // NOOP
        }
    }
}
