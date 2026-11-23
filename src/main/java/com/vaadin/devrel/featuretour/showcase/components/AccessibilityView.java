package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.upload.UploadDropZone;
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
    final Span uploadStatus = new Span(Messages.get("components.accessibility.capture.none"));
    final UploadManager uploads = new UploadManager(this);
    final UploadButton camera = new UploadButton(Messages.get("components.accessibility.capture.take"), uploads);
    final UploadFileList files = new UploadFileList(uploads);
    final UploadDropZone dropZone = new UploadDropZone(new Span(Messages.get("components.accessibility.capture.drop")), uploads);
    final TabSheet tabs = new TabSheet();
    final TextField email = new TextField(Messages.get("components.accessibility.field.email"));
    final Span tabStatus = new Span();
    final Span notificationText = new Span();

    public AccessibilityView() {
        super("Accessibility & More",
                Messages.get("components.accessibility.lead"),
                "https://vaadin.com/docs/next/components/dialog");

        addHeadingDemo();
        addCaptureDemo();
        addTabSheetDemo();
        addNotificationDemo();
        addSliderDemo();
    }

    private void addHeadingDemo() {
        var description = new Paragraph(Messages.get("components.accessibility.headings.dialogDescription"));
        dialog.setHeaderTitle(Messages.get("components.accessibility.headings.dialogTitle"));
        dialog.setHeadingLevel(2);
        dialog.add(description);
        dialog.setAriaDescribedBy(description);
        dialog.getFooter().add(new Button(Messages.get("components.accessibility.close"), e -> dialog.close()));
        dialog.setTestId("a11y-dialog");

        var confirm = new ConfirmDialog(Messages.get("components.accessibility.confirm.title"), Messages.get("components.accessibility.confirm.text"), Messages.get("components.accessibility.confirm.button"), e -> { });
        confirm.setCancelable(true);
        confirm.setHeadingLevel(2);

        var card = new Card();
        card.setTitle(Messages.get("components.accessibility.card.title"));
        card.setHeadingLevel(3);
        card.add(new Span(Messages.get("components.accessibility.card.text")));
        card.setMaxWidth("24rem");
        card.setTestId("a11y-card");

        var row = new FlexLayout(new Button(Messages.get("components.accessibility.openDialog"), e -> dialog.open()),
                new Button(Messages.get("components.accessibility.openConfirm"), e -> confirm.open()));
        row.addClassName("showcase-row");
        addDemo(Messages.get("components.accessibility.headings.heading"),
                Messages.get("components.accessibility.headings.desc"),
                new VerticalLayout(row, card), """
                        dialog.setHeaderTitle("Unsaved changes");
                        dialog.setHeadingLevel(2);
                        dialog.setAriaDescribedBy(description); // a component inside the dialog

                        confirmDialog.setHeadingLevel(2);
                        card.setHeadingLevel(3);
                        """);
    }

    private void addCaptureDemo() {
        var ui = UI.getCurrent();
        uploads.setUploadHandler(UploadHandler.inMemory((metadata, data) -> uploadStatus.setText(
                ui.getTranslation("components.accessibility.capture.received", metadata.fileName(),
                        String.valueOf(data.length))))
                .whenComplete(ok -> {
                    if (!ok) {
                        uploadStatus.setText(ui.getTranslation("components.accessibility.capture.failed"));
                    }
                }));
        uploads.addFileRemovedListener(e -> uploadStatus.setText(
                ui.getTranslation("components.accessibility.capture.removed", e.getFileName())));
        uploads.setAcceptedMimeTypes("image/*");
        uploads.setMaxFiles(3);
        camera.setCapture(UploadCapture.ENVIRONMENT);
        camera.setTestId("camera");
        files.setTestId("files");
        dropZone.setTestId("drop-zone");
        dropZone.addClassName("drop-zone");
        uploadStatus.addClassName("showcase-status");
        uploadStatus.setTestId("upload-status");

        addDemo(Messages.get("components.accessibility.capture.heading"),
                Messages.get("components.accessibility.capture.desc"),
                new VerticalLayout(camera, dropZone, files, uploadStatus), """
                        UploadManager uploads = new UploadManager(this,
                                UploadHandler.inMemory((metadata, data) -> save(data)));
                        uploads.setAcceptedMimeTypes("image/*");

                        UploadButton camera = new UploadButton("Take a photo", uploads);
                        camera.setCapture(UploadCapture.ENVIRONMENT);
                        UploadDropZone dropZone = new UploadDropZone(new Span("…or drop photos here"), uploads);
                        UploadFileList files = new UploadFileList(uploads);
                        """);
    }

    private void addTabSheetDemo() {
        tabs.add(Messages.get("components.accessibility.tabs.profile"), new TextField(Messages.get("components.accessibility.field.name")));
        tabs.add(Messages.get("components.accessibility.tabs.contact"), email);
        tabs.add(Messages.get("components.accessibility.tabs.preferences"), new TextField(Messages.get("components.accessibility.field.theme")));
        tabs.setTestId("a11y-tabs");
        email.setRequired(true);
        tabStatus.addClassName("showcase-status");
        tabStatus.setTestId("tab-status");

        var validate = new Button(Messages.get("components.accessibility.tabs.validate"), e -> {
            if (email.isEmpty()) {
                Tab tab = tabs.findTabContaining(email);
                tabs.setSelectedTab(tab);
                email.setInvalid(true);
                email.focus();
                tabStatus.setText(Messages.get("components.accessibility.tabs.found", tab.getLabel()));
            } else {
                tabStatus.setText(Messages.get("components.accessibility.tabs.ok"));
            }
        });
        validate.setTestId("validate-tabs");

        addDemo("TabSheet.findTabContaining()",
                Messages.get("components.accessibility.tabs.desc"),
                new VerticalLayout(tabs, validate, tabStatus), """
                        Tab tab = tabs.findTabContaining(email);
                        tabs.setSelectedTab(tab);
                        email.focus();
                        """);
    }

    private void addNotificationDemo() {
        notificationText.addClassName("showcase-status");
        notificationText.setTestId("notification-text");
        var show = new Button(Messages.get("components.accessibility.notification.show"), e -> {
            Notification notification = Notification.show(Messages.get("components.accessibility.notification.text"));
            notificationText.setText("notification.getText() = \"" + notification.getText() + "\"");
        });
        show.setTestId("show-notification");
        addDemo("Notification.getText()",
                Messages.get("components.accessibility.notification.desc"),
                new VerticalLayout(show, notificationText), """
                        Notification notification = Notification.show("Saved 3 items");
                        String text = notification.getText();
                        """);
    }

    private void addSliderDemo() {
        var volume = new IntegerSlider(Messages.get("components.accessibility.slider.volume"), 0, 100);
        volume.setValue(40);
        volume.setHelperText(Messages.get("components.accessibility.slider.helper"));
        volume.addThemeVariants(SliderVariant.HELPER_ABOVE);
        volume.setWidth("20rem");
        var balance = new IntegerSlider(Messages.get("components.accessibility.slider.balance"), -10, 10);
        balance.setValue(0);
        balance.addThemeVariants(SliderVariant.LABEL_ASIDE);
        balance.setWidth("20rem");
        addDemo(Messages.get("components.accessibility.slider.heading"),
                Messages.get("components.accessibility.slider.desc"),
                new VerticalLayout(volume, balance), """
                        volume.addThemeVariants(SliderVariant.HELPER_ABOVE);
                        balance.addThemeVariants(SliderVariant.LABEL_ASIDE);
                        """);
    }
}
