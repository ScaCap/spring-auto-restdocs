package capital.scalable.restdocs.example.a;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class SomeControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    protected MockMvc docMockMvc;

    @Autowired
    private WebApplicationContext context;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(resolveOutputDir());

    @Autowired
    RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Before
    public void setupMVC() {

        var resolvers = requestMappingHandlerAdapter.getArgumentResolvers();

        this.docMockMvc = MockMvcBuilders.webAppContextSetup(context)
            .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
            .apply(documentationConfiguration(restDocumentation).uris().withScheme("https")
                .withHost("127.0.0.1").withPort(443).and().snippets()
                .withDefaults(
                    CliDocumentation.curlRequest(),
                    HttpDocumentation.httpRequest(),
                    HttpDocumentation.httpResponse(),
                    AutoDocumentation.requestFields(),
                    AutoDocumentation.responseFields(),
                    AutoDocumentation.pathParameters(),
                    AutoDocumentation.requestParameters(),
//                    AutoDocumentation.modelAttribute(null),
                    AutoDocumentation.modelAttribute(resolvers),
                    AutoDocumentation.description(),
                    AutoDocumentation.methodAndPath(),
                    AutoDocumentation.requestHeaders(),

                    AutoDocumentation
                        .sectionBuilder()
                        .snippetNames(
                            SnippetRegistry.AUTO_AUTHORIZATION,
                            SnippetRegistry.AUTO_PATH_PARAMETERS,
                            SnippetRegistry.AUTO_REQUEST_PARAMETERS,
                            SnippetRegistry.AUTO_REQUEST_HEADERS,
                            SnippetRegistry.AUTO_REQUEST_FIELDS,
                            SnippetRegistry.AUTO_MODELATTRIBUTE,
                            SnippetRegistry.AUTO_RESPONSE_FIELDS,
                            SnippetRegistry.CURL_REQUEST,
                            SnippetRegistry.HTTP_RESPONSE)
                        .skipEmpty(false)
                        .build()))
            .alwaysDo(
                commonDocument()
//                document("{class-name}/{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()))
            )
            .build();
    }

    RestDocumentationResultHandler commonDocument() {
        return document("{class-name}/{method-name}",
            preprocessResponse(
                ResponseModifyingPreprocessors.replaceBinaryContent(),
                ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                prettyPrint())
        );
    }

    String resolveOutputDir() {
        String outputDir = System.getProperties().getProperty(
            "org.springframework.restdocs.outputDir");
        if (outputDir == null) {
            outputDir = "build/generated-snippets";
        }
        return outputDir;
    }

    @Test
    public void shouldDo() throws Exception {
        //given
        var someHeaderValue = "someHeaderValue";
        var someRequestText = "someRequestText";

        //when
        var resultActions = docMockMvc.perform(get(
            "/some",
            someRequestText).header("someClass", someHeaderValue));

        //then
        resultActions.
            andDo(print()).
            andExpect(status().isOk());
    }

}