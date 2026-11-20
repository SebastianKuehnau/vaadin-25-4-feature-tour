package com.vaadin.devrel.featuretour.showcase.home;

import com.vaadin.devrel.featuretour.base.ui.FeatureCards;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
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

        var eyebrow = new Span("Beta 2 · Curated by Vaadin DevRel");
        eyebrow.addClassName("home-eyebrow");
        var title = new H1("What's new in Vaadin 25.4");
        var lead = new Paragraph("A hands-on tour of the release: security, framework APIs, "
                + "components, AI and tooling. Every page is a live demo with the Java code behind it.");
        var releaseNotes = new Anchor("https://github.com/vaadin/platform/releases/tag/25.4.0-beta2", "Release notes");
        releaseNotes.setTarget(AnchorTarget.BLANK);
        var docs = new Anchor("https://vaadin.com/docs/next", "Docs for the next release");
        docs.setTarget(AnchorTarget.BLANK);
        var links = new HorizontalLayout(releaseNotes, docs);
        var hero = new Div(eyebrow, title, lead, links);
        hero.addClassName("home-hero");
        add(hero);

        // One section per area, built from the same menu tree as the side nav
        for (MenuEntry area : MenuConfiguration.getMenuEntriesTree()) {
            if (!area.children().isEmpty()) {
                var section = new Div(new H2(area.title()), new FeatureCards(area.children()));
                section.addClassName("home-section");
                add(section);
            }
        }
    }
}
