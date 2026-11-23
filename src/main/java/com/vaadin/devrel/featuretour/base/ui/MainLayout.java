package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.devrel.featuretour.base.i18n.AppI18NProvider;
import com.vaadin.devrel.featuretour.base.i18n.LocaleSupport;
import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.ScrollerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

import java.util.Locale;
import java.util.Map;

@Layout
public final class MainLayout extends AppLayout implements AfterNavigationObserver {

    static final String RELEASE_NOTES = "https://github.com/vaadin/platform/releases/tag/25.4.0-beta2";

    private static final Map<String, String> FLAGS = Map.of("en", "🇬🇧", "de", "🇩🇪", "fr", "🇫🇷", "es", "🇪🇸",
            "it", "🇮🇹", "pt", "🇵🇹");

    MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToDrawer(createApplicationHeader(), createApplicationDrawer(), createApplicationFooter());

        var breadcrumbs = new Breadcrumbs();
        breadcrumbs.addClassName("app-breadcrumbs");
        breadcrumbs.getStyle().set("flex-grow", "1");
        addToNavbar(new DrawerToggle(), breadcrumbs, createLanguageSwitcher());
    }

    private Component createApplicationHeader() {
        var logo = new Span("25.4");
        logo.addClassName("app-logo");

        var appName = new Span("Vaadin 25.4 Feature Tour");
        appName.addClassName("app-name");
        var appByline = new Span(Messages.get("app.byline"));
        appByline.addClassName("app-byline");
        var title = new Div(appName, appByline);
        title.addClassName("app-title");

        var header = new HorizontalLayout(logo, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.addClassName("app-header");
        return header;
    }

    private Component createApplicationDrawer() {
        // New in 25.4: the menu as a tree. Pages declare their area with
        // @Menu(parent = SecurityArea.class) and end up as its children here.
        var nav = new SideNav();
        for (MenuEntry entry : MenuConfiguration.getMenuEntriesTree()) {
            nav.addItem(toItem(entry));
        }

        var navs = new VerticalLayout(nav);
        navs.setPadding(false);
        navs.setSpacing(true);
        navs.addClassName("app-nav");
        var scroller = new Scroller(navs);
        scroller.addThemeVariants(ScrollerVariant.OVERFLOW_INDICATORS);
        return scroller;
    }

    static SideNavItem toItem(MenuEntry entry) {
        var item = new SideNavItem(Messages.title(entry.title()), entry.path());
        if (entry.icon() != null && !entry.icon().isBlank()) {
            String[] icon = entry.icon().split(":", 2);
            item.setPrefixComponent(icon.length == 2 ? new Icon(icon[0], icon[1]) : new Icon(icon[0]));
        }
        for (MenuEntry child : entry.children()) {
            item.addItem(toItem(child));
        }
        item.setExpanded(!entry.children().isEmpty());
        return item;
    }

    private Component createApplicationFooter() {
        var source = new Anchor(RELEASE_NOTES, Messages.get("link.releaseNotes"));
        source.setTarget(AnchorTarget.BLANK);
        var footer = new Div(new Span(Messages.get("app.footer")), source);
        footer.addClassName("app-footer");
        return footer;
    }

    private Component createLanguageSwitcher() {
        var language = new Select<Locale>();
        language.getElement().setAttribute("aria-label", Messages.get("language.label"));
        language.setItems(AppI18NProvider.LOCALES);
        // The flag stands in for the language name, which is kept as tooltip
        language.setRenderer(new ComponentRenderer<>(l -> {
            var flag = new Span(FLAGS.getOrDefault(l.getLanguage(), "🏳️"));
            flag.getElement().setAttribute("title", l.getDisplayLanguage(l));
            return flag;
        }));
        language.setWidth("5em");
        language.setValue(UI.getCurrent().getLocale());
        language.addClassName("app-language");
        language.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                LocaleSupport.change(UI.getCurrent(), e.getValue());
            }
        });
        return language;
    }

    /**
     * {@code @PageTitle} is declared in English, so the browser tab title is
     * translated here.
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        var ui = event.getLocationChangeEvent().getUI();
        var chain = ui.getInternals().getActiveRouterTargetsChain();
        if (!chain.isEmpty()) {
            var title = chain.get(0).getClass().getAnnotation(PageTitle.class);
            if (title != null) {
                ui.getPage().setTitle(Messages.title(title.value()) + " · Vaadin 25.4");
            }
        }
    }
}
