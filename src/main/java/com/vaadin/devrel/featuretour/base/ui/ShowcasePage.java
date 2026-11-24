package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
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
        // Without explicit tiers, the badges come from @ShowcaseInfo on the page class
        var info = getClass().getAnnotation(ShowcaseInfo.class);
        add(new PageHeader(title, lead, docsUrl, tiers.length == 0 && info != null ? info.tiers() : tiers));
    }

    /** Tier badges, title, lead text and a documentation link. */
    static class PageHeader extends Div {
        PageHeader(String title, String lead, String docsUrl, Tier... tiers) {
            var docs = new ExternalLink(docsUrl, "Read the docs →");
            docs.addClassName("showcase-docs");
            add(new TierBadges(tiers), new H1(title), new Paragraph(lead), docs);
            addClassName("showcase-header");
        }
    }

    /** A heading, a short explanation, the live demo and, if given, the code that builds it. */
    static class DemoSection extends Section {
        DemoSection(String heading, String description, Component demo, String code) {
            var demoArea = new Div(demo);
            demoArea.addClassName("showcase-demo");
            add(new H2(heading), new Paragraph(description), demoArea);
            if (!code.isBlank()) {
                add(new CodeDetails(code));
            }
            addClassName("showcase-section");
        }
    }

    /** A collapsed code snippet. */
    static class CodeDetails extends Details {
        CodeDetails(String code) {
            setSummaryText("Show Java code");
            add(new CodeBlock(code));
            addClassName("showcase-code-details");
        }
    }

    /** A note box. A warning note is for features that are not yet usable in this beta. */
    static class Note extends Div {
        Note(String text, boolean warning) {
            super(new Paragraph(text));
            addClassName("showcase-note");
            setClassName("warning", warning);
        }
    }

    /**
     * Adds a demo section with a heading, a short explanation, the live demo
     * and the code that builds it.
     */
    protected Section addDemo(String heading, String description, Component demo, String code) {
        var section = new DemoSection(heading, description, demo, code);
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
        var note = new Note(text, warning);
        add(note);
        return note;
    }
}
