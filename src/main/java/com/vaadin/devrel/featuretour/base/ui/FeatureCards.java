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
        for (MenuEntry entry : entries) {
            var card = new Card();
            card.setTitle(new RouterLink(entry.title(), entry.menuClass()));
            var info = entry.menuClass() == null ? null : entry.menuClass().getAnnotation(ShowcaseInfo.class);
            if (info != null) {
                card.setSubtitle(info.summary());
                var badges = new Div();
                badges.addClassName("showcase-badges");
                for (Tier tier : info.tiers()) {
                    badges.add(tier.badge());
                }
                card.add(badges);
            }
            card.addClassName("feature-card");
            add(card);
        }
    }
}
