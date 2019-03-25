/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
 * %%
 * Copyright (C) 2015 - 2019 Scalable Capital GmbH
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

import java.util.Arrays;
import java.util.List;

import jdk.javadoc.doclet.Doclet;

/**
 * A fallback option if for some reason Java removes the default "-d" option.
 */
class FallbackDirectoryOption implements Doclet.Option, Comparable<FallbackDirectoryOption> {

    private final String[] names = new String[] { "-d" };

    @Override
    public String getDescription() {
        return "The output directory";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList(names);
    }

    @Override
    public String getParameters() {
        return "directory";
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        // Do nothing. You should use this together with the wrapper to get the actual option.
        return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(names);
    }

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public int compareTo(FallbackDirectoryOption that) {
        return this.getNames().get(0).compareTo(that.getNames().get(0));
    }
}
