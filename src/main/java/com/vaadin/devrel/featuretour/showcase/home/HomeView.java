package com.vaadin.devrel.featuretour.showcase.home;

import com.vaadin.devrel.featuretour.base.ui.ExternalLink;
import com.vaadin.devrel.featuretour.base.ui.FeatureCards;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

@Route("")
@PageTitle("Home")
@Menu(title = "Overview", order = 0, icon = "vaadin:home")
public class HomeView extends VerticalLayout {

    public HomeView() {
        addClassName("home-view");
        add(new Hero());
        // One section per area, built from the same menu tree as the side nav
        for (MenuEntry area : MenuConfiguration.getMenuEntriesTree()) {
            if (!area.children().isEmpty()) {
                add(new AreaSection(area));
            }
        }
    }

    /** Title, lead text and links to the release notes and docs. */
    static class Hero extends Div {
        Hero() {
            var eyebrow = new Span("Beta 2 · Curated by Vaadin DevRel");
            eyebrow.addClassName("home-eyebrow");
            add(eyebrow,
                    new H1("What's new in Vaadin 25.4"),
                    new Paragraph("A hands-on tour of the release: security, framework APIs, components, AI and "
                            + "tooling. Every page is a live demo with the Java code behind it."),
                    new HorizontalLayout(
                            new ExternalLink("https://github.com/vaadin/platform/releases/tag/25.4.0-beta2",
                                    "Release notes"),
                            new ExternalLink("https://vaadin.com/docs/next", "Docs for the next release")));
            addClassName("home-hero");
        }
    }

    /** The pages of one area as cards. */
    static class AreaSection extends Div {
        AreaSection(MenuEntry area) {
            super(new H2(area.title()), new FeatureCards(area.children(), 3));
            addClassName("home-section");
        }
    }
}
