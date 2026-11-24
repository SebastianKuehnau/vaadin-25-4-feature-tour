package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

import java.util.List;
import java.util.Optional;

/**
 * Index page of one area (Security, Flow, ...). The pages of the area name it
 * with {@code @Menu(parent = ...)}, and this page lists its own children from
 * the menu tree.
 */
public abstract class AreaPage extends VerticalLayout {

    protected AreaPage(String title, String lead) {
        addClassName("home-view");
        var hero = new Div(new H1(title), new Paragraph(lead));
        hero.addClassName("home-hero");
        add(hero, new FeatureCards(children(), 2));
    }

    private List<MenuEntry> children() {
        return find(MenuConfiguration.getMenuEntriesTree()).map(MenuEntry::children).orElse(List.of());
    }

    private Optional<MenuEntry> find(List<MenuEntry> entries) {
        for (MenuEntry entry : entries) {
            if (entry.menuClass() == getClass()) {
                return Optional.of(entry);
            }
            var nested = find(entry.children());
            if (nested.isPresent()) {
                return nested;
            }
        }
        return Optional.empty();
    }
}
