package com.vaadin.devrel.featuretour.showcase.home;

import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
@PageTitle("Home")
public class HomeView extends VerticalLayout {

    record Feature(String title, String summary, Class<? extends Component> view, Tier... tiers) {
    }

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

        // Feature sections are added page by page
    }

    private void section(String heading, Feature... features) {
        var cards = new Div();
        cards.addClassName("card-grid");
        for (Feature feature : features) {
            var card = new Card();
            card.setTitle(new RouterLink(feature.title(), feature.view()));
            card.setSubtitle(feature.summary());
            var badges = new Div();
            badges.addClassName("showcase-badges");
            for (Tier tier : feature.tiers()) {
                badges.add(tier.badge());
            }
            card.add(badges);
            card.addClassName("feature-card");
            cards.add(card);
        }
        var section = new Div(new H2(heading), cards);
        section.addClassName("home-section");
        add(section);
    }
}
