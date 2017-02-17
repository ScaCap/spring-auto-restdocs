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

import static capital.scalable.restdocs.misc.AuthorizationSnippet.AUTHORIZATION;
import static capital.scalable.restdocs.misc.CurlRequestSnippet.CURL_REQUEST;
import static capital.scalable.restdocs.misc.HttpResponseSnippet.HTTP_RESPONSE;
import static capital.scalable.restdocs.payload.JacksonRequestFieldSnippet.REQUEST_FIELDS;
import static capital.scalable.restdocs.payload.JacksonResponseFieldSnippet.RESPONSE_FIELDS;
import static capital.scalable.restdocs.request.PathParametersSnippet.PATH_PARAMETERS;
import static capital.scalable.restdocs.request.RequestParametersSnippet.REQUEST_PARAMETERS;

import java.util.Arrays;
import java.util.Collection;

public class SectionBuilder {
    public static final Collection<String> DEFAULT_SECTIONS = Arrays.asList(
            AUTHORIZATION,
            PATH_PARAMETERS,
            REQUEST_PARAMETERS,
            REQUEST_FIELDS,
            RESPONSE_FIELDS,
            CURL_REQUEST,
            HTTP_RESPONSE
    );

    private Collection<String> sectionNames = DEFAULT_SECTIONS;
    private boolean skipEmpty = false;

    public SectionBuilder() {
    }

    public SectionBuilder sectionNames(Collection<String> sectionNames) {
        this.sectionNames = sectionNames;
        return this;
    }

    public SectionBuilder sectionNames(String... sectionNames) {
        this.sectionNames = Arrays.asList(sectionNames);
        return this;
    }

    public SectionBuilder skipEmpty(boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
        return this;
    }

    public SectionSnippet build() {
        return new SectionSnippet(sectionNames, skipEmpty);
    }
}
