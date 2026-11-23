package com.vaadin.devrel.featuretour.base;

import com.vaadin.devrel.featuretour.base.i18n.AppI18NProvider;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

class I18nTest {

    private final AppI18NProvider provider = new AppI18NProvider();

    @Test
    void every_language_has_every_key() {
        for (String bundle : AppI18NProvider.BUNDLES) {
            var english = ResourceBundle.getBundle(bundle, Locale.ENGLISH).keySet();
            for (Locale locale : provider.getProvidedLocales()) {
                assertThat(ResourceBundle.getBundle(bundle, locale).keySet()).as(bundle + " " + locale)
                        .containsAll(english);
            }
        }
    }

    @Test
    void translates_and_falls_back_to_english() {
        assertThat(provider.getTranslation("nav.security", Locale.GERMAN)).isEqualTo("Sicherheit");
        assertThat(provider.getTranslation("nav.security", Locale.JAPANESE)).isEqualTo("Security");
        assertThat(provider.getTranslation("nav.unknown", Locale.GERMAN)).isEqualTo("nav.unknown");
    }
}
