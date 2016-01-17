package capital.scalable.restdocs.jackson.payload;

import static capital.scalable.restdocs.jackson.test.SnippetMatchers.tableWithHeader;

import java.lang.reflect.Method;

import capital.scalable.restdocs.jackson.test.ExpectedSnippet;
import capital.scalable.restdocs.jackson.test.FakeMvcResult;
import capital.scalable.restdocs.jackson.test.OperationBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

public class JacksonResponseFieldSnippetTest {

    @Rule
    public final ExpectedSnippet snippet = new ExpectedSnippet();

    @Test
    public void simpleResponse() throws Exception {
        TestResource bean = new TestResource();
        Method method = TestResource.class.getMethod("getItem");
        MvcResult mvcResult = FakeMvcResult.build(bean, method);
        ObjectMapper mapper = new ObjectMapper();

        this.snippet.expectResponseFields("map-response").withContents(
                tableWithHeader("Path", "Type", "Optional", "Description")
                        .row("field1", "String", "false", "")
                        .row("field2", "Number", "true", ""));

        new JacksonResponseFieldSnippet().document(new OperationBuilder(
                "map-response", this.snippet.getOutputDirectory())
                .attribute(MvcResult.class.getName(), mvcResult)
                .attribute(ObjectMapper.class.getName(), mapper)
                .request("http://localhost")
                .build());
    }

    private class TestResource {

        public Item getItem() {
            return new Item("test");
        }
    }

    private class Item {
        @NotBlank
        private String field1;
        private Integer field2;

        public Item(String field1) {
            this.field1 = field1;
        }

        public String getField1() {
            return field1;
        }

        public Integer getField2() {
            return field2;
        }
    }
}
