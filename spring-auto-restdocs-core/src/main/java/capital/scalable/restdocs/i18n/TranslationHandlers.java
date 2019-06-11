package capital.scalable.restdocs.i18n;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import static capital.scalable.restdocs.OperationAttributeHelper.setTranslationResolver;

public abstract class TranslationHandlers {

    public static ResultHandler defaultTranslation() {
        return new TranslationPreparingResultHandler(SnippetTranslationManager.getDefaultResolver());
    }

    public static ResultHandler translation(SnippetTranslationResolver translationResolver) {
        return new TranslationPreparingResultHandler(translationResolver);
    }

    private static class TranslationPreparingResultHandler implements ResultHandler {

        private final SnippetTranslationResolver translationResolver;

        public TranslationPreparingResultHandler(SnippetTranslationResolver translationResolver) {
            this.translationResolver = translationResolver;
        }

        @Override
        public void handle(MvcResult mvcResult) throws Exception {
            setTranslationResolver(mvcResult.getRequest(), translationResolver);
        }
    }
}
