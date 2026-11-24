package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.upload.UploadCapture;
import com.vaadin.flow.component.upload.UploadTester.UploadStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CameraCaptureViewTest {

    @Autowired
    BrowserlessApplicationContext app;
    BrowserlessUIContext window;

    @BeforeEach
    void open() {
        window = app.newUser().newWindow();
        window.navigate(CameraCaptureView.class);
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    String status() {
        return window.findSpan().withTestId("upload-status").getText();
    }

    int files() {
        return window.findUploadFileList().withTestId("files").getFiles().size();
    }

    @Test
    void camera_button_asks_for_the_back_camera() {
        assertThat(window.findUploadButton().withTestId("camera").component().getCapture())
                .isEqualTo(UploadCapture.ENVIRONMENT);
    }

    @Test
    void successful_upload_reaches_the_handler() {
        var camera = window.findUploadButton().withTestId("camera");

        camera.upload("photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        assertThat(camera.getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.UPLOADED);
        assertThat(status()).isEqualTo("✅ Received photo.jpg (3 bytes)");
        assertThat(files()).isEqualTo(1);
    }

    @Test
    void failed_upload_stays_in_the_file_list() {
        var camera = window.findUploadButton().withTestId("camera");

        camera.uploadFailed("broken.jpg", "image/jpeg");

        assertThat(camera.getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.FAILED);
        assertThat(status()).isEqualTo("❌ Upload failed");
        assertThat(files()).isEqualTo(1);
    }

    @Test
    void aborted_upload_is_removed_from_the_file_list() {
        window.findUploadButton().withTestId("camera").uploadAborted("cancelled.jpg", "image/jpeg");

        assertThat(status()).isEqualTo("Removed cancelled.jpg");
        assertThat(files()).isZero();
    }

    @Test
    void files_that_are_not_images_are_rejected() {
        var camera = window.findUploadButton().withTestId("camera");

        camera.upload("notes.txt", "text/plain", new byte[]{1});

        assertThat(camera.getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.REJECTED);
    }

    @Test
    void failed_drop_on_the_drop_zone() {
        var dropZone = window.findUploadDropZone().withTestId("drop-zone");

        dropZone.uploadFailed("dropped.png", "image/png");

        assertThat(dropZone.getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.FAILED);
        assertThat(status()).isEqualTo("❌ Upload failed");
        // The drop zone and the button share one manager, and so one file list
        assertThat(files()).isEqualTo(1);
    }
}
