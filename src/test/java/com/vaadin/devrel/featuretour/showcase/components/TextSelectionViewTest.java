package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.internal.PendingJavaScriptInvocation;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.js.JsCall;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TextSelectionViewTest extends SpringBrowserlessTest {

    @Test
    void invalid_part_is_found() {
        assertThat(TextSelectionView.firstInvalidIndex("25.4.0-beta2")).isEqualTo(-1);
        assertThat(TextSelectionView.firstInvalidIndex("25.4.0-betta2")).isEqualTo(10);
        assertThat(TextSelectionView.firstInvalidIndex("25.x")).isEqualTo(3);
    }

    @Test
    void failed_validation_selects_the_invalid_part() {
        var view = navigate(TextSelectionView.class);
        TextField version = $(TextField.class).testId("version");
        test(version).setValue("25.4.0-betta2");
        UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations();

        view.validate();

        assertThat(version.isInvalid()).isTrue();
        assertThat($(Span.class).testId("version-result").getText()).contains("position 10", "\"ta2\"");
        // The selection is declared JavaScript: check the call that will be sent to the browser
        assertThat(pendingCalls()).anySatisfy(call -> {
            assertThat(call.methodName()).isEqualTo("setSelectionRange");
            assertThat(call.arguments()).containsExactly(10, 13);
        });
    }

    @Test
    void valid_version_is_saved() {
        navigate(TextSelectionView.class);
        test($(TextField.class).testId("version")).setValue("25.4.0-beta2");

        test($(com.vaadin.flow.component.button.Button.class).testId("save-version")).click();

        assertThat($(TextField.class).testId("version").isInvalid()).isFalse();
        assertThat($(Span.class).testId("version-result").getText()).isEqualTo("✅ Saved 25.4.0-beta2");
    }

    private static List<JsCall> pendingCalls() {
        return UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
                .map(PendingJavaScriptInvocation::getInvocation)
                .map(invocation -> invocation.getJsCall())
                .filter(Objects::nonNull)
                .toList();
    }
}
