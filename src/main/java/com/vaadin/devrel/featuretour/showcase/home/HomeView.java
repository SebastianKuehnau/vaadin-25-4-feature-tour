package com.vaadin.devrel.featuretour.showcase.home;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
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

        var eyebrow = new Span(Messages.get("home.eyebrow"));
        eyebrow.addClassName("home-eyebrow");
        var title = new H1(Messages.get("home.title"));
        var lead = new Paragraph(Messages.get("home.lead"));
        var releaseNotes = new Anchor("https://github.com/vaadin/platform/releases/tag/25.4.0-beta2", Messages.get("link.releaseNotes"));
        releaseNotes.setTarget(AnchorTarget.BLANK);
        var docs = new Anchor("https://vaadin.com/docs/next", Messages.get("link.docsNext"));
        docs.setTarget(AnchorTarget.BLANK);
        var links = new HorizontalLayout(releaseNotes, docs);
        var hero = new Div(eyebrow, title, lead, links);
        hero.addClassName("home-hero");
        add(hero);

        // One section per area, built from the same menu tree as the side nav
        for (MenuEntry area : MenuConfiguration.getMenuEntriesTree()) {
            if (!area.children().isEmpty()) {
                var section = new Div(new H2(Messages.title(area.title())), new FeatureCards(area.children()));
                section.addClassName("home-section");
                add(section);
            }
        }
    }
}
