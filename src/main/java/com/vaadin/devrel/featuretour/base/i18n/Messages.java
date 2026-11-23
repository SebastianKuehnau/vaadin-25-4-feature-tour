package com.vaadin.devrel.featuretour.base.i18n;

import com.vaadin.flow.component.UI;

import java.util.Locale;

/**
 * Static helpers to translate in the current UI's locale.
 */
public final class Messages {

    private Messages() {
    }

    public static String get(String key) {
        return UI.getCurrent().getTranslation(key);
    }

    /**
     * Translates a message with {@link java.text.MessageFormat} parameters
     * ({0}, {1}, ...). Apostrophes in such messages must be doubled ('').
     */
    public static String get(String key, Object... params) {
        return UI.getCurrent().getTranslation(key, params);
    }

    /**
     * The card summary of a page: {@code summary.<ClassSimpleName>}, or the
     * English text from {@code @ShowcaseInfo} when there is no translation.
     */
    public static String summary(Class<?> page, String english) {
        String key = "summary." + page.getSimpleName();
        String translated = get(key);
        return translated.equals(key) ? english : translated;
    }

    /**
     * Translates a menu or page title that is declared in English in an
     * annotation. Titles without a translation stay as declared.
     */
    public static String title(String english) {
        if (english == null) {
            return null;
        }
        String key = "nav." + english.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        String translated = get(key);
        return translated.equals(key) ? english : translated;
    }
}
