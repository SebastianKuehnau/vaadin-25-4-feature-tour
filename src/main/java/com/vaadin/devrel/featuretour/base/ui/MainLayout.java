package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.ScrollerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

@Layout
public final class MainLayout extends AppLayout {

    static final String RELEASE_NOTES = "https://github.com/vaadin/platform/releases/tag/25.4.0-beta2";

    MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToDrawer(new AppHeader(), new AppNav(), new AppFooter());

        var breadcrumbs = new Breadcrumbs();
        breadcrumbs.addClassName("app-breadcrumbs");
        addToNavbar(new DrawerToggle(), breadcrumbs);
    }

    /** Logo, app name and byline at the top of the drawer. */
    static class AppHeader extends HorizontalLayout {
        AppHeader() {
            var logo = new Span("25.4");
            logo.addClassName("app-logo");
            var appName = new Span("Vaadin 25.4 Feature Tour");
            appName.addClassName("app-name");
            var appByline = new Span("A Vaadin DevRel demo");
            appByline.addClassName("app-byline");
            var title = new Div(appName, appByline);
            title.addClassName("app-title");

            add(logo, title);
            setAlignItems(FlexComponent.Alignment.CENTER);
            setPadding(true);
            addClassName("app-header");
        }
    }

    /**
     * The side nav. New in 25.4: the menu as a tree. Pages declare their area
     * with @Menu(parent = SecurityArea.class) and end up as its children here.
     */
    static class AppNav extends Scroller {
        AppNav() {
            var nav = new SideNav();
            for (MenuEntry entry : MenuConfiguration.getMenuEntriesTree()) {
                nav.addItem(new MenuItem(entry));
            }
            var navs = new VerticalLayout(nav);
            navs.setPadding(false);
            navs.setSpacing(true);
            navs.addClassName("app-nav");
            setContent(navs);
            addThemeVariants(ScrollerVariant.OVERFLOW_INDICATORS);
        }
    }

    /** A menu entry with its icon, and its children nested below it. */
    static class MenuItem extends SideNavItem {
        MenuItem(MenuEntry entry) {
            super(entry.title(), entry.path());
            if (entry.icon() != null && !entry.icon().isBlank()) {
                String[] icon = entry.icon().split(":", 2);
                setPrefixComponent(icon.length == 2 ? new Icon(icon[0], icon[1]) : new Icon(icon[0]));
            }
            for (MenuEntry child : entry.children()) {
                addItem(new MenuItem(child));
            }
            setExpanded(!entry.children().isEmpty());
        }
    }

    static class AppFooter extends Div {
        AppFooter() {
            super(new Span("Built by Vaadin DevRel with Vaadin 25.4 and ❤️"),
                    new ExternalLink(RELEASE_NOTES, "Release notes"));
            addClassName("app-footer");
        }
    }
}
