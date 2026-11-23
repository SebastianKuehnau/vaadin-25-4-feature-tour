package com.vaadin.devrel.featuretour.base.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Translations for the app shell, the Home page and the page titles, from
 * {@code messages_xx.properties}. English is the default and the fallback.
 */
@Component
public class AppI18NProvider implements I18NProvider {

    /**
     * The shell's bundle plus one bundle per area, so areas can be translated
     * independently: {@code messages*.properties} and
     * {@code i18n/<area>*.properties}.
     */
    public static final List<String> BUNDLES = List.of("messages", "i18n.security", "i18n.flow", "i18n.components",
            "i18n.ai", "i18n.tooling");

    private static final ResourceBundle.Control NO_FALLBACK = ResourceBundle.Control
            .getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

    public static final List<Locale> LOCALES = List.of(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH,
            Locale.of("es"), Locale.ITALIAN, Locale.of("pt"));

    @Override
    public List<Locale> getProvidedLocales() {
        return LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        Locale target = locale == null ? Locale.ENGLISH : locale;
        for (String name : BUNDLES) {
            try {
                // No fallback to the JVM's default locale: unknown languages get English
                String value = ResourceBundle.getBundle(name, target, NO_FALLBACK).getString(key);
                return params.length == 0 ? value : MessageFormat.format(value, params);
            } catch (MissingResourceException e) {
                // try the next bundle
            }
        }
        return key;
    }
}
