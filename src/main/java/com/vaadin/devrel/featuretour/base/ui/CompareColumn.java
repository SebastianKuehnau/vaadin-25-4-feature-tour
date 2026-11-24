package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;

/**
 * One side of a before/after comparison. Put several of them in a
 * {@code compare-grid}.
 */
public class CompareColumn extends Div {

    public CompareColumn(String title, Component... content) {
        add(new H3(title));
        add(content);
        addClassName("compare-column");
    }

    /** A column that shows a code snippet. */
    public static CompareColumn code(String title, String code) {
        return new CompareColumn(title, new CodeBlock(code));
    }
}
