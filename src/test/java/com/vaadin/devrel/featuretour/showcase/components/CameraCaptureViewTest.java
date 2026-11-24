package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.upload.UploadButton;
import com.vaadin.flow.component.upload.UploadDropZone;
import com.vaadin.flow.component.upload.UploadFileList;
import com.vaadin.flow.component.upload.UploadTester.UploadStatus;
import com.vaadin.flow.component.upload.UploadCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CameraCaptureViewTest extends SpringBrowserlessTest {

    CameraCaptureView view;
    UploadButton camera;

    @BeforeEach
    void open() {
        view = navigate(CameraCaptureView.class);
        camera = $(UploadButton.class).testId("camera");
    }

    String status() {
        return $(Span.class).testId("upload-status").getText();
    }

    @Test
    void camera_button_asks_for_the_back_camera() {
        assertThat(camera.getCapture()).isEqualTo(UploadCapture.ENVIRONMENT);
    }

    @Test
    void successful_upload_reaches_the_handler() {
        test(camera).upload("photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        assertThat(test(camera).getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.UPLOADED);
        assertThat(status()).isEqualTo("✅ Received photo.jpg (3 bytes)");
        assertThat(test($(UploadFileList.class).testId("files")).getFiles()).hasSize(1);
    }

    @Test
    void failed_upload_stays_in_the_file_list() {
        test(camera).uploadFailed("broken.jpg", "image/jpeg");

        assertThat(test(camera).getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.FAILED);
        assertThat(status()).isEqualTo("❌ Upload failed");
        assertThat(test($(UploadFileList.class).testId("files")).getFiles()).hasSize(1);
    }

    @Test
    void aborted_upload_is_removed_from_the_file_list() {
        test(camera).uploadAborted("cancelled.jpg", "image/jpeg");

        assertThat(status()).isEqualTo("Removed cancelled.jpg");
        assertThat(test($(UploadFileList.class).testId("files")).getFiles()).isEmpty();
    }

    @Test
    void files_that_are_not_images_are_rejected() {
        test(camera).upload("notes.txt", "text/plain", new byte[]{1});

        assertThat(test(camera).getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.REJECTED);
    }

    @Test
    void failed_drop_on_the_drop_zone() {
        var dropZone = $(UploadDropZone.class).withTestId("drop-zone").single();

        test(dropZone).uploadFailed("dropped.png", "image/png");

        assertThat(test(dropZone).getLastUploadStatus()).extracting(s -> s.status()).containsExactly(UploadStatus.FAILED);
        assertThat(status()).isEqualTo("❌ Upload failed");
        // The drop zone and the button share one manager, and so one file list
        assertThat(test($(UploadFileList.class).testId("files")).getFiles()).hasSize(1);
    }
}
