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

    static final String BUNDLE = "messages";

    public static final List<Locale> LOCALES = List.of(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH,
            Locale.of("es"), Locale.ITALIAN, Locale.of("pt"));

    @Override
    public List<Locale> getProvidedLocales() {
        return LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        String value;
        try {
            value = ResourceBundle.getBundle(BUNDLE, locale == null ? Locale.ENGLISH : locale).getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
        return params.length == 0 ? value : MessageFormat.format(value, params);
    }
}
