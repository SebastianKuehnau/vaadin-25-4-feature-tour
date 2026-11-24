package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.menu.MenuEntry;

import java.util.Collection;

/**
 * A grid of cards, one per menu entry, with the summary and tier badges from
 * {@link ShowcaseInfo}.
 */
public final class FeatureCards extends Div {

    public FeatureCards(Collection<MenuEntry> entries) {
        addClassName("card-grid");
        entries.forEach(entry -> add(new FeatureCard(entry)));
    }

    /** Links to a page, with its summary and tiers when it has them. */
    static class FeatureCard extends Card {
        FeatureCard(MenuEntry entry) {
            setTitle(new RouterLink(entry.title(), entry.menuClass()));
            var info = entry.menuClass() == null ? null : entry.menuClass().getAnnotation(ShowcaseInfo.class);
            if (info != null) {
                setSubtitle(info.summary());
                add(new TierBadges(info.tiers()));
            }
            addClassName("feature-card");
        }
    }
}
