package capital.scalable.restdocs.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Inspired by
 * {@link org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver}
 */
public class SnippetTranslationResolver {

    private static ResourceBundle defaultMessages = getBundle("DefaultSnippetMessages");

    private static ResourceBundle userMessages = getBundle("SnippetMessages");

    private static ResourceBundle getBundle(String name) {
        try {
            return ResourceBundle.getBundle(
                    SnippetTranslationResolver.class.getPackage()
                            .getName() + "." + name,
                    Locale.getDefault(), Thread.currentThread().getContextClassLoader());
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    public static String translate(String key) {
        try {
            if (userMessages != null) {
                return userMessages.getString(key);
            }
        } catch (MissingResourceException ex) {
            // Continue and return default description, if available
        }
        return defaultMessages.getString(key);
    }

    // visible for testing
    public static void setUserMessages(String name) {
        userMessages = getBundle(name);
    }
}
