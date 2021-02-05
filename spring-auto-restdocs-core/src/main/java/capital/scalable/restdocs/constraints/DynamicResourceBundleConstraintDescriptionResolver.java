/*-
 * #%L
 * Spring Auto REST Docs Core
 * %%
 * Copyright (C) 2015 - 2021 Scalable Capital GmbH
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
package capital.scalable.restdocs.constraints;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

/**
 * Same as {@link org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver}
 * but with ability to evaluate constraint's message attribute before falling back to property files.
 */
public class DynamicResourceBundleConstraintDescriptionResolver implements ConstraintDescriptionResolver {

    private static final Logger log = getLogger(ConstraintAndGroupDescriptionResolver.class);

    private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

    private final ResourceBundle defaultDescriptions;

    private final ResourceBundle userDescriptions;

    public DynamicResourceBundleConstraintDescriptionResolver() {
        this(getBundle("ConstraintDescriptions"));
    }

    public DynamicResourceBundleConstraintDescriptionResolver(ResourceBundle resourceBundle) {
        this.defaultDescriptions = getBundle("DefaultConstraintDescriptions");
        this.userDescriptions = resourceBundle;
    }

    private static ResourceBundle getBundle(String name) {
        try {
            return ResourceBundle.getBundle(
                    org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver
                            .class.getPackage().getName() + "." + name,
                    Locale.getDefault(), Thread.currentThread().getContextClassLoader());
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    /**
     * First resolves based on overridden message on constraint itself, then falls back to resource bundle resolution
     */
    @Override
    public String resolveDescription(Constraint constraint) {
        String message = (String) constraint.getConfiguration().get("message");
        if (isNotBlank(message) && !message.startsWith("{")) {
            return this.propertyPlaceholderHelper.replacePlaceholders(message,
                    new ConstraintPlaceholderResolver(constraint));
        }

        try {
            String key = constraint.getName() + ".description";
            return this.propertyPlaceholderHelper.replacePlaceholders(getDescription(key),
                    new ConstraintPlaceholderResolver(constraint));
        } catch (MissingResourceException e) {
            log.debug("No description found for constraint {}: {}.", constraint.getName(), e.getMessage());
            return "";
        }

    }

    private String getDescription(String key) {
        try {
            if (this.userDescriptions != null) {
                return this.userDescriptions.getString(key);
            }
        } catch (MissingResourceException ex) {
            // Continue and return default description, if available
        }
        return this.defaultDescriptions.getString(key);
    }

    private static final class ConstraintPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final Constraint constraint;

        private ConstraintPlaceholderResolver(Constraint constraint) {
            this.constraint = constraint;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            Object replacement = this.constraint.getConfiguration().get(placeholderName);
            if (replacement == null) {
                return null;
            }
            if (replacement.getClass().isArray()) {
                return StringUtils.arrayToDelimitedString((Object[]) replacement, ", ");
            }
            return replacement.toString();
        }

    }

}
