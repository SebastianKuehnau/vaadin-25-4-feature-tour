package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.regex.Pattern;

@Route("text-selection")
@PageTitle("Text Selection")
@Menu(title = "Text Selection", parent = ComponentsArea.class, order = 4)
@ShowcaseInfo(summary = "setSelectionRange(), selectAll() and setCursorPosition() from the server.", tiers = Tier.FREE)
public class TextSelectionView extends ShowcasePage {

    static final Pattern VERSION = Pattern.compile("\\d+\\.\\d+\\.\\d+(-(alpha|beta|rc)\\d+)?");

    final TextField version = new TextField(Messages.get("components.textselection.version"));
    final Span result = new Span();
    final TextField playground = new TextField(Messages.get("components.textselection.playground"));

    public TextSelectionView() {
        super("Text Selection",
                Messages.get("components.textselection.lead"),
                "https://vaadin.com/docs/next/components/text-field");

        addValidationDemo();
        addPlayground();
    }

    private void addValidationDemo() {
        version.setValue("25.4.0-betta2");
        version.setWidth("20rem");
        version.setTestId("version");
        result.addClassName("showcase-status");
        result.setTestId("version-result");
        var save = new Button(Messages.get("components.textselection.save"), e -> validate());
        save.addThemeVariants(ButtonVariant.PRIMARY);
        save.setTestId("save-version");

        addDemo(Messages.get("components.textselection.validation.heading"),
                Messages.get("components.textselection.validation.desc"),
                new VerticalLayout(new FlexLayout(version, save), result), """
                        int bad = firstInvalidIndex(version.getValue());
                        if (bad >= 0) {
                            version.setInvalid(true);
                            version.focus();
                            version.setSelectionRange(bad, version.getValue().length());
                        }
                        """);
    }

    void validate() {
        String value = version.getValue();
        int bad = firstInvalidIndex(value);
        if (bad < 0) {
            version.setInvalid(false);
            result.setText(Messages.get("components.textselection.saved", value));
            return;
        }
        version.setErrorMessage(Messages.get("components.textselection.error"));
        version.setInvalid(true);
        version.focus();
        version.setSelectionRange(bad, value.length());
        result.setText(Messages.get("components.textselection.invalid", bad, value.substring(bad)));
    }

    /** The index of the first character after the longest valid prefix, or -1 if the value is valid. */
    static int firstInvalidIndex(String value) {
        if (VERSION.matcher(value).matches()) {
            return -1;
        }
        for (int end = value.length(); end > 0; end--) {
            var matcher = VERSION.matcher(value.substring(0, end));
            if (matcher.matches() || matcher.hitEnd()) {
                return end;
            }
        }
        return 0;
    }

    private void addPlayground() {
        playground.setValue("Vaadin 25.4.0-beta2 feature tour");
        playground.setWidth("24rem");
        playground.setTestId("playground");
        var all = new Button("selectAll()", e -> {
            playground.focus();
            playground.selectAll();
        });
        var beta = new Button(Messages.get("components.textselection.selectBeta"), e -> {
            int start = playground.getValue().indexOf("beta2");
            playground.focus();
            playground.setSelectionRange(start, start + "beta2".length());
        });
        var start = new Button(Messages.get("components.textselection.cursorStart"), e -> {
            playground.focus();
            playground.setCursorPosition(0);
        });
        var end = new Button(Messages.get("components.textselection.cursorEnd"), e -> {
            playground.focus();
            playground.setCursorPosition(playground.getValue().length());
        });
        var row = new FlexLayout(all, beta, start, end);
        row.addClassName("showcase-row");
        addDemo(Messages.get("components.textselection.playground"),
                Messages.get("components.textselection.playground.desc"),
                new VerticalLayout(playground, row), """
                        field.focus();
                        field.selectAll();
                        field.setSelectionRange(start, end);
                        field.setCursorPosition(0);
                        """);
    }
}
