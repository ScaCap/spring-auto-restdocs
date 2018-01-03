package capital.scalable.restdocs.i18n;

import org.junit.rules.ExternalResource;

public class TranslationRule extends ExternalResource {

    @Override
    protected void after() {
        SnippetTranslationResolver.setUserMessages(null);
    }

    public void setTestTranslations() {
        SnippetTranslationResolver.setUserMessages("TestSnippetMessages");
    }
}
