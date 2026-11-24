package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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

    public MultiSelectView() {
        super("Multi-Select Combo Box",
                "A Select all button above the items selects or deselects all of them, or only the ones that match "
                        + "the filter you typed.",
                "https://vaadin.com/docs/next/components/multi-select-combo-box");

        addInMemoryDemo();
        addLazyDemo();
    }

    /** Countries to pick from, with the Select All button turned on. */
    static class CountryPicker extends MultiSelectComboBox<String> {
        CountryPicker(String label, String testId) {
            super(label);
            setSelectAllButtonVisible(true);
            setWidth("28rem");
            setTestId(testId);
        }
    }

    private void addInMemoryDemo() {
        var countries = new CountryPicker("Countries", "countries");
        countries.setItems(COUNTRIES);
        var selection = new StatusText("selection", "0 selected");
        countries.addValueChangeListener(e -> selection.setText(e.getValue().size() + " selected"));

        addDemo("Select all, or all filtered items",
                "Open the dropdown and press Select All. Then clear, type \"land\": the button turns into Select Filtered, "
                        + "and only the six matching countries are selected. The others keep their state.",
                new VerticalLayout(countries, selection), """
                        class CountryPicker extends MultiSelectComboBox<String> {
                            CountryPicker(String label) {
                                super(label);
                                **setSelectAllButtonVisible(true)**;
                                setWidth("28rem");
                            }
                        }

                        var countries = new CountryPicker("Countries");
                        countries.setItems(COUNTRIES);
                        """);
    }

    private void addLazyDemo() {
        var lazy = new CountryPicker("Countries (lazy data provider)", "countries-lazy");
        lazy.setItems(query -> COUNTRIES.stream()
                .filter(c -> c.toLowerCase(Locale.ROOT).contains(query.getFilter().orElse("").toLowerCase(Locale.ROOT)))
                .skip(query.getOffset()).limit(query.getLimit()));

        addDemo("Hidden with lazy data",
                "The same setting on a combo box with a lazy data provider: the button stays hidden, because selecting "
                        + "all would mean fetching every item from the backend. isSelectAllButtonVisible() still returns "
                        + lazy.isSelectAllButtonVisible() + ", it reports the setting, not the effective visibility.",
                lazy, """
                        var lazy = new CountryPicker("Countries (lazy data provider)");
                        lazy.setItems(query -> service.fetch(query.getFilter(), query.getOffset(), query.getLimit()));
                        // setSelectAllButtonVisible(true) from CountryPicker has no effect with lazy data
                        """);
    }
}
