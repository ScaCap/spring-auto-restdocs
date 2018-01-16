/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2018 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package capital.scalable.restdocs.i18n;

import java.text.MessageFormat;
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

    public static String translate(String key, Object... args) {
        try {
            if (userMessages != null) {
                return format(userMessages.getString(key), args);
            }
        } catch (MissingResourceException ex) {
            // Continue and return default description, if available
        }
        return format(defaultMessages.getString(key), args);
    }

    // visible for testing
    static void setUserMessages(String name) {
        userMessages = getBundle(name);
    }

    private static String format(String message, Object[] args) {
        return new MessageFormat(message).format(args);
    }
}
