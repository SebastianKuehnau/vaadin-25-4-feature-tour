package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.DisableOnClickMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("button")
@PageTitle("Button")
@Menu(title = "Button", parent = ComponentsArea.class, order = 5)
@ShowcaseInfo(summary = "DisableOnClickMode.UNTIL_RESPONSE re-enables the button when the server is done.", tiers = Tier.FREE)
public class ButtonView extends ShowcasePage {

    static final long SLOW_MILLIS = 2000;

    final Button plain = new Button(Messages.get("components.button.save.none"));
    final Button untilEnabled = new Button(Messages.get("components.button.save.with", "setDisableOnClick(true)"));
    final Button untilResponse = new Button(Messages.get("components.button.save.with", "UNTIL_RESPONSE"));
    final Span plainCount = new Span(saves(0));
    final Span enabledCount = new Span(saves(0));
    final Span responseCount = new Span(saves(0));
    int plainSaves;
    int enabledSaves;
    int responseSaves;

    public ButtonView() {
        super("Button",
                Messages.get("components.button.lead"),
                "https://vaadin.com/docs/next/components/button");

        plain.addClickListener(e -> plainCount.setText(saves(++plainSaves) + slowSave()));
        plain.setTestId("plain");

        untilEnabled.setDisableOnClick(true);
        untilEnabled.addClickListener(e -> enabledCount.setText(
                Messages.get("components.button.disabledUntil", saves(++enabledSaves) + slowSave())));
        untilEnabled.setTestId("until-enabled");
        var reEnable = new Button("setEnabled(true)", e -> untilEnabled.setEnabled(true));

        untilResponse.setDisableOnClick(DisableOnClickMode.UNTIL_RESPONSE);
        untilResponse.addClickListener(e -> responseCount.setText(saves(++responseSaves) + slowSave()));
        untilResponse.setTestId("until-response");
        responseCount.setTestId("response-count");

        var compare = new Div(
                column(Messages.get("components.button.col1.title"), Messages.get("components.button.col1.text"),
                        plain, plainCount),
                column(Messages.get("components.button.col2.title"), Messages.get("components.button.col2.text"),
                        untilEnabled, enabledCount, reEnable),
                column(Messages.get("components.button.col3.title"), Messages.get("components.button.col3.text"),
                        untilResponse, responseCount));
        compare.addClassName("compare-grid");

        addDemo(Messages.get("components.button.heading"),
                Messages.get("components.button.desc"),
                compare, """
                        Button save = new Button("Save");
                        save.setDisableOnClick(DisableOnClickMode.UNTIL_RESPONSE);
                        save.addClickListener(e -> service.save()); // slow

                        // Before 25.4: UNTIL_ENABLED, the default for setDisableOnClick(true)
                        save.setDisableOnClick(true);
                        save.addClickListener(e -> {
                            try { service.save(); } finally { save.setEnabled(true); }
                        });
                        """);
    }

    private static String saves(int count) {
        return Messages.get(count == 1 ? "components.button.saves.one" : "components.button.saves.other", count);
    }

    private static String slowSave() {
        try {
            Thread.sleep(SLOW_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "";
    }

    private static Div column(String title, String text, com.vaadin.flow.component.Component... content) {
        var column = new Div(new H3(title), new Paragraph(text));
        column.add(content);
        column.addClassName("compare-column");
        column.addClassName("button-column");
        return column;
    }
}
