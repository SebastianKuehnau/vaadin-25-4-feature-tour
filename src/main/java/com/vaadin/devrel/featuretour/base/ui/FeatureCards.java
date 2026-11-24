package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.menu.MenuEntry;

import java.util.Collection;

/**
 * A grid of cards, one per menu entry, with the summary and tier badges from
 * {@link ShowcaseInfo}. The whole card is a link to the page.
 */
public final class FeatureCards extends Div {

    /**
     * @param headingLevel the heading level of the card titles, one below the
     *                     heading the cards are under
     */
    public FeatureCards(Collection<MenuEntry> entries, int headingLevel) {
        addClassName("card-grid");
        entries.forEach(entry -> add(new FeatureLink(entry, headingLevel)));
    }

    /** A RouterLink around the card, as the navigation docs suggest for links with rich content. */
    static class FeatureLink extends RouterLink {
        FeatureLink(MenuEntry entry, int headingLevel) {
            super(entry.menuClass());
            add(new FeatureCard(entry, headingLevel));
            addClassName("feature-link");
        }
    }

    /** The page's title, summary and tiers when it has them. */
    static class FeatureCard extends Card {
        FeatureCard(MenuEntry entry, int headingLevel) {
            setTitle(entry.title());
            setHeadingLevel(headingLevel);
            var info = entry.menuClass() == null ? null : entry.menuClass().getAnnotation(ShowcaseInfo.class);
            if (info != null) {
                setSubtitle(info.summary());
                add(new TierBadges(info.tiers()));
            }
            addClassName("feature-card");
        }
    }
}
