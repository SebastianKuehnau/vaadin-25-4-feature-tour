package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.internal.PendingJavaScriptInvocation;
import com.vaadin.flow.js.JsCall;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TextSelectionViewTest {

    @Autowired
    BrowserlessApplicationContext app;
    BrowserlessUIContext window;
    TextSelectionView view;

    @BeforeEach
    void open() {
        window = app.newUser().newWindow();
        view = window.navigate(TextSelectionView.class);
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    String result() {
        return window.findSpan().withTestId("version-result").getText();
    }

    @Test
    void invalid_part_is_found() {
        assertThat(TextSelectionView.firstInvalidIndex("25.4.0-beta2")).isEqualTo(-1);
        assertThat(TextSelectionView.firstInvalidIndex("25.4.0-betta2")).isEqualTo(10);
        assertThat(TextSelectionView.firstInvalidIndex("25.x")).isEqualTo(3);
    }

    @Test
    void failed_validation_selects_the_invalid_part() {
        var version = window.findTextField().withTestId("version");
        version.setValue("25.4.0-betta2");
        pendingCalls();

        view.versionForm.validate();

        assertThat(version.component().isInvalid()).isTrue();
        assertThat(version.component().getErrorMessage()).startsWith("\"ta2\" doesn't fit");
        assertThat(result()).as("one error message, in the field").isEmpty();
        // The selection is declared JavaScript: check the call that will be sent to the browser
        assertThat(pendingCalls()).anySatisfy(call -> {
            assertThat(call.methodName()).isEqualTo("setSelectionRange");
            assertThat(call.arguments()).containsExactly(10, 13);
        });
    }

    @Test
    void valid_version_is_saved() {
        var version = window.findTextField().withTestId("version");
        version.setValue("25.4.0-beta2");

        window.findButton().withTestId("save-version").click();

        assertThat(version.component().isInvalid()).isFalse();
        assertThat(result()).isEqualTo("✅ Saved 25.4.0-beta2");
    }

    @Test
    void editing_the_value_clears_the_previous_result() {
        var version = window.findTextField().withTestId("version");
        version.setValue("25.4.0");
        window.findButton().withTestId("save-version").click();
        assertThat(result()).isEqualTo("✅ Saved 25.4.0");

        version.setValue("25.4.1");

        assertThat(result()).isEmpty();
    }

    /** Takes the JavaScript calls queued for the browser since the last call. */
    private List<JsCall> pendingCalls() {
        return window.getUI().getInternals().dumpPendingJavaScriptInvocations().stream()
                .map(PendingJavaScriptInvocation::getInvocation)
                .map(invocation -> invocation.getJsCall())
                .filter(Objects::nonNull)
                .toList();
    }
}
