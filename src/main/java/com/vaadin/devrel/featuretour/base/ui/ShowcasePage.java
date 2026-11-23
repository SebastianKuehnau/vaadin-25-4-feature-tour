package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Base class for all showcase pages: a header with title, tier badges, lead
 * text and a documentation link, followed by demo sections. Every section shows
 * a live demo next to a collapsible Java snippet.
 */
public abstract class ShowcasePage extends VerticalLayout {

    protected ShowcasePage(String title, String lead, String docsUrl, Tier... tiers) {
        addClassName("showcase-page");
        setPadding(true);
        setSpacing(false);

        var badges = new FlexLayout();
        badges.addClassName("showcase-badges");
        // Without explicit tiers, the badges come from @ShowcaseInfo on the page class
        var info = getClass().getAnnotation(ShowcaseInfo.class);
        for (Tier tier : tiers.length == 0 && info != null ? info.tiers() : tiers) {
            badges.add(tier.badge());
        }

        var docs = new Anchor(docsUrl, Messages.get("link.docs"));
        docs.setTarget(AnchorTarget.BLANK);
        docs.addClassName("showcase-docs");

        var header = new Div(badges, new H1(Messages.title(title)), new Paragraph(lead), docs);
        header.addClassName("showcase-header");
        add(header);
    }

    /**
     * Adds a demo section with a heading, a short explanation, the live demo
     * and the code that builds it.
     */
    protected Section addDemo(String heading, String description, Component demo, String code) {
        var demoArea = new Div(demo);
        demoArea.addClassName("showcase-demo");

        var section = new Section(new H2(heading), new Paragraph(description), demoArea);
        section.addClassName("showcase-section");
        if (!code.isBlank()) {
            var pre = new Pre(code.strip());
            pre.addClassName("showcase-code");
            var details = new Details(Messages.get("code.show"), pre);
            details.addClassName("showcase-code-details");
            section.add(details);
        }
        add(section);
        return section;
    }

    /**
     * Adds a note box, e.g. for setup hints.
     */
    protected Div addNote(String text) {
        return addNote(text, false);
    }

    /**
     * Adds a note box. A warning note is used for features that are not yet
     * usable in this beta.
     */
    protected Div addNote(String text, boolean warning) {
        var note = new Div(new Paragraph(text));
        note.addClassName("showcase-note");
        if (warning) {
            note.addClassName("warning");
        }
        add(note);
        return note;
    }
}
