package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;

/**
 * A link that opens in a new tab, such as one to the documentation.
 */
public class ExternalLink extends Anchor {

    public ExternalLink(String href, String text) {
        super(href, text);
        setTarget(AnchorTarget.BLANK);
    }
}
