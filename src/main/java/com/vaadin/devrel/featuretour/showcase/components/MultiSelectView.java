package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

@Route("multi-select-combo-box")
@PageTitle("Multi-Select Combo Box")
@Menu(title = "Multi-Select Combo Box", parent = ComponentsArea.class, order = 3)
@ShowcaseInfo(summary = "A Select all button that also selects just the filtered items.", tiers = Tier.FREE)
public class MultiSelectView extends ShowcasePage {

    static final List<String> COUNTRIES = List.of("Argentina", "Australia", "Austria", "Belgium", "Brazil", "Canada",
            "Chile", "Denmark", "Estonia", "Finland", "France", "Germany", "India", "Ireland", "Italy", "Japan",
            "Netherlands", "New Zealand", "Norway", "Poland", "Portugal", "Spain", "Sweden", "Switzerland",
            "United Kingdom", "United States");

    final MultiSelectComboBox<String> countries = new MultiSelectComboBox<>("Countries");
    final Span selection = new Span();
    final MultiSelectComboBox<String> lazy = new MultiSelectComboBox<>("Countries (lazy data provider)");

    public MultiSelectView() {
        super("Multi-Select Combo Box",
                "A Select all button above the items selects or deselects all of them, or only the ones that match "
                        + "the filter you typed.",
                "https://vaadin.com/docs/next/components/multi-select-combo-box");

        addInMemoryDemo();
        addLazyDemo();
    }

    private void addInMemoryDemo() {
        countries.setItems(COUNTRIES);
        countries.setSelectAllButtonVisible(true);
        countries.setWidth("28rem");
        countries.setTestId("countries");
        selection.addClassName("showcase-status");
        selection.setTestId("selection");
        selection.setText("0 selected");
        countries.addValueChangeListener(e -> selection.setText(e.getValue().size() + " selected"));

        addDemo("Select all, or all filtered items",
                "Open the dropdown and press Select All. Then clear, type \"land\": the button turns into Select Filtered, "
                        + "and only the six matching countries are selected. The others keep their state.",
                new VerticalLayout(countries, selection), """
                        MultiSelectComboBox<String> countries = new MultiSelectComboBox<>("Countries");
                        countries.setItems(COUNTRIES);
                        countries.setSelectAllButtonVisible(true);
                        """);
    }

    private void addLazyDemo() {
        lazy.setItems(query -> COUNTRIES.stream()
                .filter(c -> c.toLowerCase(Locale.ROOT).contains(query.getFilter().orElse("").toLowerCase(Locale.ROOT)))
                .skip(query.getOffset()).limit(query.getLimit()));
        lazy.setSelectAllButtonVisible(true);
        lazy.setWidth("28rem");
        lazy.setTestId("countries-lazy");

        addDemo("Hidden with lazy data",
                "The same setting on a combo box with a lazy data provider: the button stays hidden, because selecting "
                        + "all would mean fetching every item from the backend. isSelectAllButtonVisible() still returns "
                        + lazy.isSelectAllButtonVisible() + ", it reports the setting, not the effective visibility.",
                lazy, """
                        lazy.setItems(query -> service.fetch(query.getFilter(), query.getOffset(), query.getLimit()));
                        lazy.setSelectAllButtonVisible(true); // no effect with lazy data
                        """);
    }
}
