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

import static capital.scalable.restdocs.OperationAttributeHelper.setPostmanCollection;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

public abstract class PostmanResultHandlers {

    public static ResultHandler preparePostman(PostmanCollection postmanCollection) {
        return new PostmanCollectionResultHandler(postmanCollection);
    }

    private static class PostmanCollectionResultHandler implements ResultHandler {
        private PostmanCollection postmanCollection;

        public PostmanCollectionResultHandler(PostmanCollection postmanCollection) {
            this.postmanCollection = postmanCollection;
        }

        @Override
        public void handle(MvcResult result) throws Exception {
            setPostmanCollection(result.getRequest(), postmanCollection);
        }
    }
}
