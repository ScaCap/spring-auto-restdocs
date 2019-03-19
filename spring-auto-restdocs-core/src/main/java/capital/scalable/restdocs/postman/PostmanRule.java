/*-
 * #%L
 * Spring Auto REST Docs Core
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
package capital.scalable.restdocs.postman;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.rules.ExternalResource;

public class PostmanRule extends ExternalResource {
    private final PostmanCollection postmanCollection;
    private final File outputDirectory;

    public PostmanRule(String name, String description) {
        this(name, description, getDefaultOutputDirectory());
    }

    public PostmanRule(String name, String description, String outputDirectory) {
        this.postmanCollection = new PostmanCollection(name, description);
        this.outputDirectory = new File(outputDirectory);
    }

    public PostmanCollection get() {
        return postmanCollection;
    }

    @Override
    protected void after() {
        ObjectMapper objectMapper = setupMapper();
        String json = null;
        try {
            File file = new File(outputDirectory, postmanCollection.info.name + ".json");
            Files.createDirectories(outputDirectory.toPath());
            objectMapper.writerFor(PostmanCollection.class).writeValue(file, postmanCollection);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ObjectMapper setupMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibilityChecker(
                objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(ANY)
                        .withGetterVisibility(NONE)
                        .withSetterVisibility(NONE)
                        .withCreatorVisibility(NONE));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    private static String getDefaultOutputDirectory() {
        if (new File("pom.xml").exists()) {
            return "target/generated-collections";
        }
        return "build/generated-collections";
    }
}
