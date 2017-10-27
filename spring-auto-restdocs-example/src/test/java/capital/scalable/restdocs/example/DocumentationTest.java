package capital.scalable.restdocs.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import capital.scalable.restdocs.example.testsupport.MockMvcBase;
import org.junit.Test;

public class DocumentationTest extends MockMvcBase {

    @Test
    public void docsForwarding() throws Exception {
        mockMvc.perform(get("/docs"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/docs/index.html"));
    }
}
