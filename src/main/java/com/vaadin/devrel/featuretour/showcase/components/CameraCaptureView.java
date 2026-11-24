package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.UploadButton;
import com.vaadin.flow.component.upload.UploadCapture;
import com.vaadin.flow.component.upload.UploadDropZone;
import com.vaadin.flow.component.upload.UploadFileList;
import com.vaadin.flow.component.upload.UploadManager;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;

@Route("camera-capture")
@PageTitle("Camera Capture")
@Menu(title = "Camera Capture", parent = ComponentsArea.class, order = 8)
@ShowcaseInfo(summary = "UploadButton.setCapture() opens the phone camera, with a shared drop zone and file list.", tiers = Tier.FREE)
public class CameraCaptureView extends ShowcasePage {

    final Span uploadStatus = new Span("No photo yet.");
    final UploadManager uploads = new UploadManager(this);
    final UploadButton camera = new UploadButton("Take a photo", uploads);
    final UploadFileList files = new UploadFileList(uploads);
    final UploadDropZone dropZone = new UploadDropZone(new Span("…or drop photos here"), uploads);

    public CameraCaptureView() {
        super("Camera Capture",
                "Let phone users take a photo straight from your upload button, and combine the button with a drop zone "
                        + "and a file list that all share one UploadManager.",
                "https://vaadin.com/docs/next/components/upload");

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
        dropZone.setTestId("drop-zone");
        dropZone.addClassName("drop-zone");
        uploadStatus.addClassName("showcase-status");
        uploadStatus.setTestId("upload-status");

        addDemo("UploadButton.setCapture()",
                "On phones, the button opens the camera directly: ENVIRONMENT for the back camera, USER for the front. "
                        + "On desktop it opens the normal file dialog.",
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
}
