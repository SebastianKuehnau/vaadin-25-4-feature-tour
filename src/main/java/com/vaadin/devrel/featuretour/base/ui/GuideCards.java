package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;

/**
 * Cards for guided pages without a live demo: a title, a short text and
 * optional steps to try.
 */
public final class GuideCards extends Div {

    public GuideCards() {
        addClassName("card-grid");
        addClassName("guide-cards");
    }

    public GuideCards card(String title, String text, String... steps) {
        var card = new Card();
        card.setTitle(title);
        card.add(new Paragraph(text));
        if (steps.length > 0) {
            var list = new OrderedList();
            for (String step : steps) {
                list.add(new ListItem(step));
            }
            card.add(list);
        }
        card.addClassName("guide-card");
        add(card);
        return this;
    }
}
