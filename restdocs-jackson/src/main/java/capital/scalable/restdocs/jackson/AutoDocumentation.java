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

package capital.scalable.restdocs.jackson;

import capital.scalable.restdocs.jackson.misc.AuthorizationSnippet;
import capital.scalable.restdocs.jackson.misc.DescriptionSnippet;
import capital.scalable.restdocs.jackson.misc.MethodAndPathSnippet;
import capital.scalable.restdocs.jackson.misc.SectionSnippet;
import capital.scalable.restdocs.jackson.payload.JacksonRequestFieldSnippet;
import capital.scalable.restdocs.jackson.payload.JacksonResponseFieldSnippet;
import capital.scalable.restdocs.jackson.request.PathParametersSnippet;
import capital.scalable.restdocs.jackson.request.RequestParametersSnippet;
import org.springframework.restdocs.snippet.Snippet;

/**
 * @author Florian Benz, Juraj Misur
 */
public abstract class AutoDocumentation {

    private AutoDocumentation() {
    }

    public static Snippet requestFields() {
        return new JacksonRequestFieldSnippet();
    }

    public static Snippet responseFields() {
        return new JacksonResponseFieldSnippet();
    }

    public static Snippet pathParameters() {
        return new PathParametersSnippet();
    }

    public static Snippet requestParameters() {
        return new RequestParametersSnippet();
    }

    public static Snippet description() {
        return new DescriptionSnippet();
    }

    public static Snippet methodAndPath() {
        return new MethodAndPathSnippet();
    }

    public static Snippet section() {
        return new SectionSnippet();
    }

    public static Snippet authorization(String defaultAuthorization) {
        return new AuthorizationSnippet(defaultAuthorization);
    }
}
