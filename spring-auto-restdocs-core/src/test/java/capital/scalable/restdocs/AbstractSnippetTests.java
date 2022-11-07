/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2022 Scalable Capital GmbH
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

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.GeneratedSnippets;
import org.springframework.restdocs.testfixtures.OperationBuilder;
import org.springframework.restdocs.testfixtures.SnippetConditions;

@RunWith(Parameterized.class)
public abstract class AbstractSnippetTests {

    protected final TemplateFormat templateFormat;

    @Rule
    public GeneratedSnippets generatedSnippets;

    @Rule
    public OperationBuilder operationBuilder;

    @Parameters(name = "{0}")
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[] { "Asciidoctor", TemplateFormats.asciidoctor() },
                new Object[] { "Markdown", TemplateFormats.markdown() });
    }

    public AbstractSnippetTests(String name, TemplateFormat templateFormat) {
        this.generatedSnippets = new GeneratedSnippets(templateFormat);
        this.templateFormat = templateFormat;
        this.operationBuilder = new OperationBuilder(this.templateFormat);
    }

    public SnippetConditions.TableCondition<?> tableWithHeader(String... headers) {
        return SnippetConditions.tableWithHeader(this.templateFormat, headers);
    }
}

