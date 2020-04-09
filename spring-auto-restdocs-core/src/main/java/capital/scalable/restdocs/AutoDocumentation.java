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
package capital.scalable.restdocs;

import java.util.Collection;

import capital.scalable.restdocs.hypermedia.EmbeddedSnippet;
import capital.scalable.restdocs.hypermedia.LinksSnippet;
import capital.scalable.restdocs.misc.AuthorizationSnippet;
import capital.scalable.restdocs.misc.DescriptionSnippet;
import capital.scalable.restdocs.misc.MethodAndPathSnippet;
import capital.scalable.restdocs.payload.JacksonModelAttributeSnippet;
import capital.scalable.restdocs.payload.JacksonRequestFieldSnippet;
import capital.scalable.restdocs.payload.JacksonResponseFieldSnippet;
import capital.scalable.restdocs.request.PathParametersSnippet;
import capital.scalable.restdocs.request.RequestHeaderSnippet;
import capital.scalable.restdocs.request.RequestParametersSnippet;
import capital.scalable.restdocs.section.SectionBuilder;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

public abstract class AutoDocumentation {

    private AutoDocumentation() {
    }

    public static JacksonRequestFieldSnippet requestFields() {
        return new JacksonRequestFieldSnippet();
    }

    public static JacksonModelAttributeSnippet modelAttribute(Collection<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers) {
        return new JacksonModelAttributeSnippet(handlerMethodArgumentResolvers, false);
    }

    public static JacksonResponseFieldSnippet responseFields() {
        return new JacksonResponseFieldSnippet();
    }

    public static PathParametersSnippet pathParameters() {
        return new PathParametersSnippet();
    }

    public static RequestHeaderSnippet requestHeaders() {
        return new RequestHeaderSnippet();
    }

    public static RequestParametersSnippet requestParameters() {
        return new RequestParametersSnippet();
    }

    public static DescriptionSnippet description() {
        return new DescriptionSnippet();
    }

    public static MethodAndPathSnippet methodAndPath() {
        return new MethodAndPathSnippet();
    }

    public static Snippet section() {
        return new SectionBuilder().build();
    }

    public static SectionBuilder sectionBuilder() {
        return new SectionBuilder();
    }

    public static Snippet authorization(String defaultAuthorization) {
        return new AuthorizationSnippet(defaultAuthorization);
    }

    public static LinksSnippet links() {
        return new LinksSnippet();
    }

    public static EmbeddedSnippet embedded() {
        return new EmbeddedSnippet();
    }
}
