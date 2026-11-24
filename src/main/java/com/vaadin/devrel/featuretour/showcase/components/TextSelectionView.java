package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    final VersionForm versionForm = new VersionForm();

    public TextSelectionView() {
        super("Text Selection",
                "Select text or move the cursor in Text Field, Text Area, Password Field and Big Decimal Field from "
                        + "Java. All of them implement HasTextSelection.",
                "https://vaadin.com/docs/next/components/text-field");

        addValidationDemo();
        addPlayground();
    }

    /** A version field with a Save button. Failed validation selects the part of the value to fix. */
    static class VersionForm extends VerticalLayout {
        final TextField version = new TextField("Vaadin version", "25.4.0-betta2", "");
        final StatusText result = new StatusText("version-result");

        VersionForm() {
            version.setWidth("20rem");
            version.setTestId("version");
            version.setErrorMessage("Expected something like 25.4.0 or 25.4.0-beta2");
            var save = new Button("Save", e -> validate());
            save.addThemeVariants(ButtonVariant.PRIMARY);
            save.setTestId("save-version");
            var row = new HorizontalLayout(version, save);
            row.setAlignItems(Alignment.BASELINE);
            add(row, result);
        }

        void validate() {
            String value = version.getValue();
            int bad = firstInvalidIndex(value);
            if (bad < 0) {
                version.setInvalid(false);
                result.setText("✅ Saved " + value);
                return;
            }
            version.setInvalid(true);
            version.focus();
            version.setSelectionRange(bad, value.length());
            result.setText("❌ Invalid from position " + bad + ": \"" + value.substring(bad) + "\" is selected");
        }
    }

    private void addValidationDemo() {
        addDemo("Point at the invalid part after validation",
                "Press Save: the validator finds where the value stops matching and selects the rest, so the user "
                        + "sees exactly what to fix and can type over it.",
                versionForm, """
                        class VersionForm extends VerticalLayout {
                            final TextField version = new TextField("Vaadin version", "25.4.0-betta2", "");

                            void validate() {
                                String value = version.getValue();
                                int bad = firstInvalidIndex(value);
                                if (bad >= 0) {
                                    version.setInvalid(true);
                                    version.focus();
                                    version.**setSelectionRange(bad, value.length())**;
                                }
                            }
                        }
                        """);
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

    /** Buttons that select text or move the cursor in a field. Each focuses the field first. */
    static class SelectionButtons extends FlexLayout {
        final TextField field;

        SelectionButtons(TextField field) {
            this.field = field;
            add(action("selectAll()", field::selectAll),
                    action("Select \"beta2\"", () -> {
                        int start = field.getValue().indexOf("beta2");
                        field.setSelectionRange(start, start + "beta2".length());
                    }),
                    action("Cursor to start", () -> field.setCursorPosition(0)),
                    action("Cursor to end", () -> field.setCursorPosition(field.getValue().length())));
            addClassName("showcase-row");
        }

        private Button action(String text, Runnable action) {
            return new Button(text, e -> {
                field.focus();
                action.run();
            });
        }
    }

    private void addPlayground() {
        var playground = new TextField("Playground", "Vaadin 25.4.0-beta2 feature tour", "");
        playground.setWidth("24rem");
        playground.setTestId("playground");
        addDemo("Playground",
                "Focus the field first: browsers only show a selection in the focused field.",
                new VerticalLayout(playground, new SelectionButtons(playground)), """
                        class SelectionButtons extends FlexLayout {
                            SelectionButtons(TextField field) {
                                add(action("selectAll()", field::**selectAll**),
                                        action("Select \"beta2\"", () -> {
                                            int start = field.getValue().indexOf("beta2");
                                            field.**setSelectionRange(start, start + "beta2".length())**;
                                        }),
                                        action("Cursor to start", () -> field.**setCursorPosition(0)**),
                                        action("Cursor to end", () -> field.**setCursorPosition(field.getValue().length())**));
                            }

                            private Button action(String text, Runnable action) {
                                return new Button(text, e -> {
                                    field.focus(); // browsers only show a selection in the focused field
                                    action.run();
                                });
                            }
                        }
                        """);
    }
}
