package com.vaadin.devrel.featuretour.base.i18n;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Picks the UI locale: the language chosen in the switcher (kept in a cookie),
 * then the browser language, then English.
 */
@Component
public class LocaleSupport implements VaadinServiceInitListener {

    static final String COOKIE = "tour-lang";

    @Override
    public void serviceInit(com.vaadin.flow.server.ServiceInitEvent event) {
        event.getSource().addUIInitListener(e -> e.getUI().setLocale(initialLocale(VaadinRequest.getCurrent(),
                e.getUI().getLocale())));
    }

    static Locale initialLocale(VaadinRequest request, Locale browser) {
        Optional<Locale> chosen = request == null || request.getCookies() == null ? Optional.empty()
                : Arrays.stream(request.getCookies()).filter(c -> COOKIE.equals(c.getName()))
                        .map(c -> supported(c.getValue())).flatMap(Optional::stream).findFirst();
        return chosen.or(() -> supported(browser == null ? null : browser.getLanguage())).orElse(Locale.ENGLISH);
    }

    private static Optional<Locale> supported(String language) {
        return AppI18NProvider.LOCALES.stream().filter(l -> l.getLanguage().equals(language)).findFirst();
    }

    /**
     * Switches the language of the UI, remembers it and reloads, because the
     * pages build their texts when they are created.
     */
    public static void change(UI ui, Locale locale) {
        VaadinResponse response = VaadinService.getCurrentResponse();
        if (response != null) {
            var cookie = new Cookie(COOKIE, locale.getLanguage());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365);
            response.addCookie(cookie);
        }
        ui.setLocale(locale);
        ui.getPage().reload();
    }
}
