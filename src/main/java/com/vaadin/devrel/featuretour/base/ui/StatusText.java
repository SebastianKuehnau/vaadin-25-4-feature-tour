package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.html.Span;

/**
 * A one-line result of a demo, such as "3 saves" or "Copied".
 */
public class StatusText extends Span {

    public StatusText(String testId) {
        this(testId, "");
    }

    public StatusText(String testId, String text) {
        super(text);
        addClassName("showcase-status");
        setTestId(testId);
    }
}
