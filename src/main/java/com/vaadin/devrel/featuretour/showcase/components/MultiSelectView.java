package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
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

    final MultiSelectComboBox<String> countries = new MultiSelectComboBox<>(Messages.get("components.multiselect.countries"));
    final Span selection = new Span();
    final MultiSelectComboBox<String> lazy = new MultiSelectComboBox<>(Messages.get("components.multiselect.lazy"));

    public MultiSelectView() {
        super("Multi-Select Combo Box",
                Messages.get("components.multiselect.lead"),
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
        selection.setText(Messages.get("components.multiselect.selected", 0));
        countries.addValueChangeListener(e -> selection.setText(Messages.get("components.multiselect.selected", e.getValue().size())));

        addDemo(Messages.get("components.multiselect.inmemory.heading"),
                Messages.get("components.multiselect.inmemory.desc"),
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

        addDemo(Messages.get("components.multiselect.lazy.heading"),
                Messages.get("components.multiselect.lazy.desc", String.valueOf(lazy.isSelectAllButtonVisible())),
                lazy, """
                        lazy.setItems(query -> service.fetch(query.getFilter(), query.getOffset(), query.getLimit()));
                        lazy.setSelectAllButtonVisible(true); // no effect with lazy data
                        """);
    }
}
