package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

/**
 * A route that always fails, so the lifecycle dashboard can show a failed
 * navigation. Not in the menu.
 */
@Route("lifecycle-events/broken")
public class BrokenView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        throw new IllegalStateException("This route fails on purpose");
    }
}
