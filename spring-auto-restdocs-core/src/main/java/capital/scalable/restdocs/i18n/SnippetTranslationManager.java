package capital.scalable.restdocs.i18n;


public class SnippetTranslationManager {

    private static SnippetTranslationResolver snippetTranslationResolver = new ResourceBundleSnippetTranslationResolver();

    public static void withTranslationResolver(SnippetTranslationResolver translationResolver) {
        snippetTranslationResolver = translationResolver;
    }

    public static String translate(String key, Object... args) {
        return snippetTranslationResolver.translate(key, args);
    }
}
