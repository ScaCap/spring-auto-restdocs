package capital.scalable.restdocs.i18n;


public class SnippetTranslationManager {

    private static SnippetTranslationResolver snippetTranslationResolver;

    public static synchronized SnippetTranslationResolver getDefaultResolver() {
        if (snippetTranslationResolver == null) {
            snippetTranslationResolver = new ResourceBundleSnippetTranslationResolver();
        }
        return snippetTranslationResolver;
    }
}
