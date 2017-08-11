package capital.scalable.restdocs.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.SnippetException;

public class FieldDescriptorUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void assertAllDocumented() throws Exception {
        List<FieldDescriptor> fields = new ArrayList<>();
        fields.add(fieldWithPath("1").description(""));
        fields.add(fieldWithPath("2").description("something"));
        fields.add(fieldWithPath("2"));

        thrown.expect(SnippetException.class);
        thrown.expectMessage("Following fields were not documented: [1, 2]");

        FieldDescriptorUtil.assertAllDocumented(fields, "fields");
    }
}