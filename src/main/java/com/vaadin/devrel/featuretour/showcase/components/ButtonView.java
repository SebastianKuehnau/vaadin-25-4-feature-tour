package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.CompareColumn;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
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

    public ButtonView() {
        super("Double-Click Protection",
                "DisableOnClickMode.UNTIL_RESPONSE disables the button on click and enables it again automatically "
                        + "with the next response, once the click listeners have run. Each listener here takes 2 seconds.",
                "https://vaadin.com/docs/next/components/button");

        var plain = new SlowSaveButton("Save (no protection)", "plain");
        var untilEnabled = new SlowSaveButton("Save (setDisableOnClick(true))", "until-enabled");
        untilEnabled.setDisableOnClick(true);
        var untilResponse = new SlowSaveButton("Save (UNTIL_RESPONSE)", "until-response");
        untilResponse.setDisableOnClick(DisableOnClickMode.UNTIL_RESPONSE);

        var compare = new Div(
                new ButtonColumn("Before: no protection", "Double-click: both clicks are queued, so you save twice.",
                        plain, plain.count),
                new ButtonColumn("Before: setDisableOnClick(true)",
                        "Safe, but you must enable the button yourself, also on errors.",
                        untilEnabled, untilEnabled.count,
                        new Button("setEnabled(true)", e -> untilEnabled.setEnabled(true))),
                new ButtonColumn("25.4: UNTIL_RESPONSE", "Disabled while the server works, enabled again by the response.",
                        untilResponse, untilResponse.count));
        compare.addClassName("compare-grid");

        addDemo("Double-click each button",
                "All three listeners sleep for 2 seconds to simulate a slow save.",
                compare, """
                        var untilResponse = new SlowSaveButton("Save (UNTIL_RESPONSE)");
                        untilResponse.**setDisableOnClick(DisableOnClickMode.UNTIL_RESPONSE)**;

                        // Before 25.4: UNTIL_ENABLED, the default for setDisableOnClick(true).
                        // You must call setEnabled(true) yourself, also when the save fails.
                        var untilEnabled = new SlowSaveButton("Save (setDisableOnClick(true))");
                        untilEnabled.setDisableOnClick(true);

                        class SlowSaveButton extends Button {
                            SlowSaveButton(String text) {
                                super(text);
                                addClickListener(e -> service.save()); // takes 2 seconds
                            }
                        }
                        """);
    }

    /** A save button whose listener takes 2 seconds, with a count of the saves it made. */
    static class SlowSaveButton extends Button {
        final StatusText count;
        int saves;

        SlowSaveButton(String text, String testId) {
            super(text);
            setTestId(testId);
            count = new StatusText(testId + "-count", "0 saves");
            addClickListener(e -> {
                slowSave();
                saves++;
                count.setText(saves + (saves == 1 ? " save" : " saves"));
            });
        }

        private static void slowSave() {
            try {
                Thread.sleep(SLOW_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** One way to protect a button, explained, next to the button itself. */
    static class ButtonColumn extends CompareColumn {
        ButtonColumn(String title, String text, Component... content) {
            super(title, new Paragraph(text));
            add(content);
            addClassName("button-column");
        }
    }
}
