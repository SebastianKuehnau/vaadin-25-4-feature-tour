package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.html.Div;

/**
 * The license tiers of a feature as a row of badges.
 */
public class TierBadges extends Div {

    public TierBadges(Tier... tiers) {
        addClassName("showcase-badges");
        for (Tier tier : tiers) {
            add(tier.badge());
        }
    }
}
