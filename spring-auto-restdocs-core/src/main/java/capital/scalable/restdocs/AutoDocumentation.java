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

package capital.scalable.restdocs;

import capital.scalable.restdocs.misc.AuthorizationSnippet;
import capital.scalable.restdocs.misc.DescriptionSnippet;
import capital.scalable.restdocs.misc.MethodAndPathSnippet;
import capital.scalable.restdocs.payload.JacksonRequestFieldSnippet;
import capital.scalable.restdocs.payload.JacksonResponseFieldSnippet;
import capital.scalable.restdocs.request.PathParametersSnippet;
import capital.scalable.restdocs.request.RequestParametersSnippet;
import capital.scalable.restdocs.section.SectionBuilder;
import org.springframework.restdocs.snippet.Snippet;

public abstract class AutoDocumentation {

    private AutoDocumentation() {
    }

    public static Snippet requestFields() {
        return new JacksonRequestFieldSnippet();
    }

    public static Snippet requestFields(boolean failOnUndocumentedFields) {
        return new JacksonRequestFieldSnippet(failOnUndocumentedFields);
    }

    public static Snippet responseFields() {
        return new JacksonResponseFieldSnippet();
    }

    public static Snippet responseFields(boolean failOnUndocumentedFields) {
        return new JacksonResponseFieldSnippet(failOnUndocumentedFields);
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
        return new SectionBuilder().build();
    }

    public static SectionBuilder sectionBuilder() {
        return new SectionBuilder();
    }

    public static Snippet authorization(String defaultAuthorization) {
        return new AuthorizationSnippet(defaultAuthorization);
    }
}
