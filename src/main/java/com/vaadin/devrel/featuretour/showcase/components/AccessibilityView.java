package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
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
import com.vaadin.flow.component.upload.UploadButton;
import com.vaadin.flow.component.upload.UploadCapture;
import com.vaadin.flow.component.upload.UploadFileList;
import com.vaadin.flow.component.upload.UploadManager;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;

@Route("accessibility-and-more")
@PageTitle("Accessibility & More")
@Menu(title = "Accessibility & More", parent = ComponentsArea.class, order = 8)
@ShowcaseInfo(summary = "Heading levels, aria-describedby, camera capture, findTabContaining(), Slider variants.", tiers = Tier.FREE)
public class AccessibilityView extends ShowcasePage {

    final Dialog dialog = new Dialog();
    final Span uploadStatus = new Span("No photo yet.");
    final UploadManager uploads = new UploadManager(this);
    final UploadButton camera = new UploadButton("Take a photo", uploads);
    final UploadFileList files = new UploadFileList(uploads);
    final TabSheet tabs = new TabSheet();
    final TextField email = new TextField("Email");
    final Span tabStatus = new Span();
    final Span notificationText = new Span();

    public AccessibilityView() {
        super("Accessibility & More",
                "Smaller improvements across the components, many of them for screen reader users.",
                "https://vaadin.com/docs/next/components/dialog");

        addHeadingDemo();
        addCaptureDemo();
        addTabSheetDemo();
        addNotificationDemo();
        addSliderDemo();
    }

    private void addHeadingDemo() {
        var description = new Paragraph("Your changes will be lost unless you save them first.");
        dialog.setHeaderTitle("Unsaved changes");
        dialog.setHeadingLevel(2);
        dialog.add(description);
        dialog.setAriaDescribedBy(description);
        dialog.getFooter().add(new Button("Close", e -> dialog.close()));
        dialog.setTestId("a11y-dialog");

        var confirm = new ConfirmDialog("Delete project?", "This can't be undone.", "Delete", e -> { });
        confirm.setCancelable(true);
        confirm.setHeadingLevel(2);

        var card = new Card();
        card.setTitle("Card with an h3 title");
        card.setHeadingLevel(3);
        card.add(new Span("Card titles fit into your page outline instead of using a fixed level."));
        card.setMaxWidth("24rem");
        card.setTestId("a11y-card");

        var row = new FlexLayout(new Button("Open dialog", e -> dialog.open()),
                new Button("Open confirm dialog", e -> confirm.open()));
        row.addClassName("showcase-row");
        addDemo("Heading levels and aria-describedby",
                "Dialog, Confirm Dialog and Card render their title with the heading level you choose, so screen reader "
                        + "users can navigate by headings. The dialog also announces its description with aria-describedby.",
                new VerticalLayout(row, card), """
                        dialog.setHeaderTitle("Unsaved changes");
                        dialog.setHeadingLevel(2);
                        dialog.setAriaDescribedBy(description); // a component inside the dialog

                        confirmDialog.setHeadingLevel(2);
                        card.setHeadingLevel(3);
                        """);
    }

    private void addCaptureDemo() {
        uploads.setUploadHandler(UploadHandler.inMemory((metadata, data) -> uploadStatus.setText(
                "✅ Received " + metadata.fileName() + " (" + data.length + " bytes)"))
                .whenComplete(ok -> {
                    if (!ok) {
                        uploadStatus.setText("❌ Upload failed");
                    }
                }));
        uploads.addFileRemovedListener(e -> uploadStatus.setText("Removed " + e.getFileName()));
        uploads.setAcceptedMimeTypes("image/*");
        uploads.setMaxFiles(3);
        camera.setCapture(UploadCapture.ENVIRONMENT);
        camera.setTestId("camera");
        files.setTestId("files");
        uploadStatus.addClassName("showcase-status");
        uploadStatus.setTestId("upload-status");

        addDemo("Upload: camera capture",
                "On phones, the button opens the camera directly: ENVIRONMENT for the back camera, USER for the front. "
                        + "On desktop it opens the normal file dialog.",
                new VerticalLayout(camera, files, uploadStatus), """
                        UploadManager uploads = new UploadManager(this,
                                UploadHandler.inMemory((metadata, data) -> save(data)));
                        uploads.setAcceptedMimeTypes("image/*");

                        UploadButton camera = new UploadButton("Take a photo", uploads);
                        camera.setCapture(UploadCapture.ENVIRONMENT);
                        UploadFileList files = new UploadFileList(uploads);
                        """);
    }

    private void addTabSheetDemo() {
        tabs.add("Profile", new TextField("Name"));
        tabs.add("Contact", email);
        tabs.add("Preferences", new TextField("Theme"));
        tabs.setTestId("a11y-tabs");
        email.setRequired(true);
        tabStatus.addClassName("showcase-status");
        tabStatus.setTestId("tab-status");

        var validate = new Button("Validate", e -> {
            if (email.isEmpty()) {
                Tab tab = tabs.findTabContaining(email);
                tabs.setSelectedTab(tab);
                email.setInvalid(true);
                email.focus();
                tabStatus.setText("The email field is on tab \"" + tab.getLabel() + "\", selected it for you.");
            } else {
                tabStatus.setText("All good.");
            }
        });
        validate.setTestId("validate-tabs");

        addDemo("TabSheet.findTabContaining()",
                "Validation failed on a field in another tab? Find the tab that contains it, even when it is nested "
                        + "deeper, and switch to it. TabSheet also got removeAll().",
                new VerticalLayout(tabs, validate, tabStatus), """
                        Tab tab = tabs.findTabContaining(email);
                        tabs.setSelectedTab(tab);
                        email.focus();
                        """);
    }

    private void addNotificationDemo() {
        notificationText.addClassName("showcase-status");
        notificationText.setTestId("notification-text");
        var show = new Button("Show notification", e -> {
            Notification notification = Notification.show("Saved 3 items");
            notificationText.setText("notification.getText() = \"" + notification.getText() + "\"");
        });
        show.setTestId("show-notification");
        addDemo("Notification.getText()",
                "Read back the text of a notification, handy in tests and when re-showing it.",
                new VerticalLayout(show, notificationText), """
                        Notification notification = Notification.show("Saved 3 items");
                        String text = notification.getText();
                        """);
    }

    private void addSliderDemo() {
        var volume = new IntegerSlider("Volume", 0, 100);
        volume.setValue(40);
        volume.setHelperText("Helper text above the slider");
        volume.addThemeVariants(SliderVariant.HELPER_ABOVE);
        volume.setWidth("20rem");
        var balance = new IntegerSlider("Balance", -10, 10);
        balance.setValue(0);
        balance.addThemeVariants(SliderVariant.LABEL_ASIDE);
        balance.setWidth("20rem");
        addDemo("Slider theme variants",
                "HELPER_ABOVE puts the helper text between label and slider, LABEL_ASIDE puts the label next to it.",
                new VerticalLayout(volume, balance), """
                        volume.addThemeVariants(SliderVariant.HELPER_ABOVE);
                        balance.addThemeVariants(SliderVariant.LABEL_ASIDE);
                        """);
    }
}
