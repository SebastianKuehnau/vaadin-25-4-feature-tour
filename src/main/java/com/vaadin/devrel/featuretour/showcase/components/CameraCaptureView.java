package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
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

    public CameraCaptureView() {
        super("Camera Capture",
                "Let phone users take a photo straight from your upload button, and combine the button with a drop zone "
                        + "and a file list that all share one UploadManager.",
                "https://vaadin.com/docs/next/components/upload");

        addDemo("UploadButton.setCapture()",
                "On phones, the button opens the camera directly: ENVIRONMENT for the back camera, USER for the front. "
                        + "On desktop it opens the normal file dialog.",
                new PhotoUploader(), """
                        class PhotoUploader extends VerticalLayout {
                            final UploadManager uploads = new UploadManager(this);

                            PhotoUploader() {
                                uploads.setUploadHandler(UploadHandler.inMemory((metadata, data) -> save(data)));
                                uploads.setAcceptedMimeTypes("image/*");
                                uploads.setMaxFiles(3);
                                // Three components, one manager, one list of files
                                add(new CameraButton(uploads), new PhotoDropZone(uploads), new UploadFileList(uploads));
                            }
                        }

                        class CameraButton extends UploadButton {
                            CameraButton(UploadManager uploads) {
                                super("Take a photo", uploads);
                                **setCapture(UploadCapture.ENVIRONMENT)**; // USER for the front camera
                            }
                        }

                        class PhotoDropZone extends UploadDropZone {
                            PhotoDropZone(UploadManager uploads) {
                                super(new Span("…or drop photos here"), uploads);
                            }
                        }
                        """);
    }

    /** Up to three photos, from the camera or dropped, in one shared file list. */
    static class PhotoUploader extends VerticalLayout {
        final StatusText status = new StatusText("upload-status", "No photo yet.");
        final UploadManager uploads = new UploadManager(this);

        PhotoUploader() {
            uploads.setUploadHandler(UploadHandler.inMemory((metadata, data) -> status.setText(
                            "✅ Received " + metadata.fileName() + " (" + data.length + " bytes)"))
                    .whenComplete(ok -> {
                        if (!ok) {
                            status.setText("❌ Upload failed");
                        }
                    }));
            uploads.addFileRemovedListener(e -> status.setText("Removed " + e.getFileName()));
            uploads.setAcceptedMimeTypes("image/*");
            uploads.setMaxFiles(3);

            var files = new UploadFileList(uploads);
            files.setTestId("files");
            add(new CameraButton(uploads), new PhotoDropZone(uploads), files, status);
        }
    }

    /** Opens the back camera on phones. */
    static class CameraButton extends UploadButton {
        CameraButton(UploadManager uploads) {
            super("Take a photo", uploads);
            setCapture(UploadCapture.ENVIRONMENT);
            setTestId("camera");
        }
    }

    static class PhotoDropZone extends UploadDropZone {
        PhotoDropZone(UploadManager uploads) {
            super(new Span("…or drop photos here"), uploads);
            addClassName("drop-zone");
            setTestId("drop-zone");
        }
    }
}
