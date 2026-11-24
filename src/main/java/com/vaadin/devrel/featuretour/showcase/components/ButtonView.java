package com.vaadin.devrel.featuretour.showcase.components;

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

@Route("double-click-protection")
@PageTitle("Double-Click Protection")
@Menu(title = "Double-Click Protection", parent = ComponentsArea.class, order = 5)
@ShowcaseInfo(summary = "DisableOnClickMode.UNTIL_RESPONSE re-enables the button when the server is done.", tiers = Tier.FREE)
public class ButtonView extends ShowcasePage {

    static final long SLOW_MILLIS = 2000;

    final Button plain = new Button("Save (no protection)");
    final Button untilEnabled = new Button("Save (setDisableOnClick(true))");
    final Button untilResponse = new Button("Save (UNTIL_RESPONSE)");
    final Span plainCount = new Span("0 saves");
    final Span enabledCount = new Span("0 saves");
    final Span responseCount = new Span("0 saves");
    int plainSaves;
    int enabledSaves;
    int responseSaves;

    public ButtonView() {
        super("Double-Click Protection",
                "DisableOnClickMode.UNTIL_RESPONSE disables the button on click and enables it again automatically "
                        + "with the next response, once the click listeners have run. Each listener here takes 2 seconds.",
                "https://vaadin.com/docs/next/components/button");

        plain.addClickListener(e -> plainCount.setText(saves(++plainSaves) + slowSave()));
        plain.setTestId("plain");

        untilEnabled.setDisableOnClick(true);
        untilEnabled.addClickListener(e -> enabledCount.setText(saves(++enabledSaves) + slowSave()
                + " · stays disabled until setEnabled(true)"));
        untilEnabled.setTestId("until-enabled");
        var reEnable = new Button("setEnabled(true)", e -> untilEnabled.setEnabled(true));

        untilResponse.setDisableOnClick(DisableOnClickMode.UNTIL_RESPONSE);
        untilResponse.addClickListener(e -> responseCount.setText(saves(++responseSaves) + slowSave()));
        untilResponse.setTestId("until-response");
        responseCount.setTestId("response-count");

        var compare = new Div(
                column("Before: no protection", "Double-click: both clicks are queued, so you save twice.",
                        plain, plainCount),
                column("Before: setDisableOnClick(true)", "Safe, but you must enable the button yourself, also on errors.",
                        untilEnabled, enabledCount, reEnable),
                column("25.4: UNTIL_RESPONSE", "Disabled while the server works, enabled again by the response.",
                        untilResponse, responseCount));
        compare.addClassName("compare-grid");

        addDemo("Double-click each button",
                "All three listeners sleep for 2 seconds to simulate a slow save.",
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
        return count + (count == 1 ? " save" : " saves");
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
