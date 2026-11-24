package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.component.slider.SliderVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("component-polish")
@PageTitle("Component Polish")
@Menu(title = "Component Polish", parent = ComponentsArea.class, order = 9)
@ShowcaseInfo(summary = "Heading levels, aria-describedby, findTabContaining(), Notification.getText(), Slider variants.", tiers = Tier.FREE)
public class ComponentPolishView extends ShowcasePage {

    public ComponentPolishView() {
        super("Component Polish",
                "Heading levels and descriptions for screen reader users, jumping to the tab that holds an invalid field, "
                        + "reading a notification's text and new Slider variants.",
                "https://vaadin.com/docs/next/components/dialog");

        addHeadingDemo();
        addTabSheetDemo();
        addNotificationDemo();
        addSliderDemo();
    }

    /** A dialog with an h2 title, which announces its description with aria-describedby. */
    static class UnsavedChangesDialog extends Dialog {
        UnsavedChangesDialog() {
            var description = new Paragraph("Your changes will be lost unless you save them first.");
            setHeaderTitle("Unsaved changes");
            setHeadingLevel(2);
            add(description);
            setAriaDescribedBy(description);
            getFooter().add(new Button("Close", e -> close()));
            setTestId("a11y-dialog");
        }
    }

    static class DeleteProjectDialog extends ConfirmDialog {
        DeleteProjectDialog() {
            super("Delete project?", "This can't be undone.", "Delete", e -> { });
            setCancelable(true);
            setHeadingLevel(2);
        }
    }

    static class H3Card extends Card {
        H3Card() {
            setTitle("Card with an h3 title");
            setHeadingLevel(3);
            add(new Span("Card titles fit into your page outline instead of using a fixed level."));
            setMaxWidth("24rem");
            setTestId("a11y-card");
        }
    }

    private void addHeadingDemo() {
        var dialog = new UnsavedChangesDialog();
        var confirm = new DeleteProjectDialog();
        var row = new FlexLayout(new Button("Open dialog", e -> dialog.open()),
                new Button("Open confirm dialog", e -> confirm.open()));
        row.addClassName("showcase-row");
        addDemo("Heading levels and aria-describedby",
                "Dialog, Confirm Dialog and Card render their title with the heading level you choose, so screen reader "
                        + "users can navigate by headings. The dialog also announces its description with aria-describedby.",
                new VerticalLayout(row, new H3Card()), """
                        class UnsavedChangesDialog extends Dialog {
                            UnsavedChangesDialog() {
                                var description = new Paragraph("Your changes will be lost unless you save them first.");
                                setHeaderTitle("Unsaved changes");
                                **setHeadingLevel(2)**;
                                add(description);
                                **setAriaDescribedBy(description)**; // a component inside the dialog
                            }
                        }

                        class DeleteProjectDialog extends ConfirmDialog {
                            DeleteProjectDialog() {
                                super("Delete project?", "This can't be undone.", "Delete", e -> { });
                                **setHeadingLevel(2)**;
                            }
                        }

                        class H3Card extends Card {
                            H3Card() {
                                setTitle("Card with an h3 title");
                                **setHeadingLevel(3)**;
                            }
                        }
                        """);
    }

    /** A form split into tabs. Validating it jumps to the tab of the invalid field. */
    static class TabbedForm extends VerticalLayout {
        final TabSheet tabs = new TabSheet();
        final TextField email = new TextField("Email");
        final StatusText status = new StatusText("tab-status");

        TabbedForm() {
            email.setRequired(true);
            tabs.add("Profile", new TextField("Name"));
            tabs.add("Contact", email);
            tabs.add("Preferences", new TextField("Theme"));
            tabs.setTestId("a11y-tabs");
            var validate = new Button("Validate", e -> validate());
            validate.setTestId("validate-tabs");
            add(tabs, validate, status);
        }

        void validate() {
            if (email.isEmpty()) {
                Tab tab = tabs.findTabContaining(email);
                tabs.setSelectedTab(tab);
                email.setInvalid(true);
                email.focus();
                status.setText("The email field is on tab \"" + tab.getLabel() + "\", selected it for you.");
            } else {
                status.setText("All good.");
            }
        }
    }

    private void addTabSheetDemo() {
        addDemo("TabSheet.findTabContaining()",
                "Validation failed on a field in another tab? Find the tab that contains it, even when it is nested "
                        + "deeper, and switch to it. TabSheet also got removeAll().",
                new TabbedForm(), """
                        class TabbedForm extends VerticalLayout {
                            final TabSheet tabs = new TabSheet();
                            final TextField email = new TextField("Email");

                            void validate() {
                                if (email.isEmpty()) {
                                    Tab tab = tabs.**findTabContaining(email)**;
                                    tabs.setSelectedTab(tab);
                                    email.setInvalid(true);
                                    email.focus();
                                }
                            }
                        }
                        """);
    }

    private void addNotificationDemo() {
        var notificationText = new StatusText("notification-text");
        var show = new Button("Show notification", e -> {
            Notification notification = Notification.show("Saved 3 items");
            notificationText.setText("notification.getText() = \"" + notification.getText() + "\"");
        });
        show.setTestId("show-notification");
        addDemo("Notification.getText()",
                "Read back the text of a notification, handy in tests and when re-showing it.",
                new VerticalLayout(show, notificationText), """
                        Notification notification = Notification.show("Saved 3 items");
                        String text = notification.**getText()**;
                        """);
    }

    /** A 20rem wide slider with a start value and a theme variant. */
    static class VariantSlider extends IntegerSlider {
        VariantSlider(String label, int min, int max, int value, SliderVariant variant) {
            super(label, min, max);
            setValue(value);
            addThemeVariants(variant);
            setWidth("20rem");
        }
    }

    private void addSliderDemo() {
        var volume = new VariantSlider("Volume", 0, 100, 40, SliderVariant.HELPER_ABOVE);
        volume.setHelperText("Helper text above the slider");
        addDemo("Slider theme variants",
                "HELPER_ABOVE puts the helper text between label and slider, LABEL_ASIDE puts the label next to it.",
                new VerticalLayout(volume, new VariantSlider("Balance", -10, 10, 0, SliderVariant.LABEL_ASIDE)), """
                        class VariantSlider extends IntegerSlider {
                            VariantSlider(String label, int min, int max, int value, SliderVariant variant) {
                                super(label, min, max);
                                setValue(value);
                                addThemeVariants(variant);
                            }
                        }

                        var volume = new VariantSlider("Volume", 0, 100, 40, **SliderVariant.HELPER_ABOVE**);
                        volume.setHelperText("Helper text above the slider");
                        var balance = new VariantSlider("Balance", -10, 10, 0, **SliderVariant.LABEL_ASIDE**);
                        """);
    }
}
