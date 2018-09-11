/*-
 * #%L
 * Spring Auto REST Docs Json Doclet for JDK9+
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
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

import jdk.javadoc.doclet.Doclet;

import java.util.Arrays;
import java.util.List;

public abstract class DirectoryLocationOption implements Doclet.Option, Comparable<DirectoryLocationOption> {

    private final String[] names;
    private final String argumentName;
    private final int argumentCount;
    private final String description;
    private final String parameters;

    public DirectoryLocationOption() {
        this.argumentName = "-d";
        this.argumentCount = 1;
        this.names = new String[]{argumentName};
        this.description = "Directory location";
        this.parameters = "-d";
    }

    @Override
    public String getDescription() {
        return description;
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
        return parameters;
    }

    @Override
    public String toString() {
        return Arrays.toString(names);
    }

    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    @Override
    public int compareTo(DirectoryLocationOption that) {
        return this.getNames().get(0).compareTo(that.getNames().get(0));
    }


}
