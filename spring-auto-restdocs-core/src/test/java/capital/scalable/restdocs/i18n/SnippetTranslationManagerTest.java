package capital.scalable.restdocs.i18n;


import org.junit.AfterClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnippetTranslationManagerTest {

    @AfterClass
    public static void rollbackToDefault(){
        SnippetTranslationManager.withTranslationResolver(new ResourceBundleSnippetTranslationResolver());
    }

    @Test
    public void callDefaultTranslationResolver() {
        assertThat(SnippetTranslationManager.translate("authorization")).isEqualTo("Authorization");
    }

    @Test
    public void callAnotherTranslationResolver(){
        SnippetTranslationManager.withTranslationResolver(new TestSnippetTranslationResolver());

        assertThat(SnippetTranslationManager.translate("authorization")).isEqualTo("Test Authorization");
        assertThat(SnippetTranslationManager.translate("path-parameters")).isNull();
    }


    private class TestSnippetTranslationResolver implements SnippetTranslationResolver {

        @Override
        public String translate(String key, Object... args) {
            if (key.equals("authorization")) {
                return "Test Authorization";
            }
            return null;
        }
    }
}
