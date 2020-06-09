/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
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
package capital.scalable.restdocs.jsondoclet;

import java.util.List;

import jdk.javadoc.doclet.Doclet;

/**
 * A wrapper around another {@link jdk.javadoc.doclet.Doclet.Option}, allowing use to get the arguments as well.
 */
class WrappingOption implements Doclet.Option {

    private final Doclet.Option internal;

    WrappingOption(Doclet.Option internal) {
        this.internal = internal;
    }

    @Override
    public int getArgumentCount() {
        return internal.getArgumentCount();
    }

    @Override
    public String getDescription() {
        return internal.getDescription();
    }

    @Override
    public Kind getKind() {
        return internal.getKind();
    }

    @Override
    public List<String> getNames() {
        return internal.getNames();
    }

    @Override
    public String getParameters() {
        return internal.getParameters();
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        return internal.process(option, arguments);
    }
}
