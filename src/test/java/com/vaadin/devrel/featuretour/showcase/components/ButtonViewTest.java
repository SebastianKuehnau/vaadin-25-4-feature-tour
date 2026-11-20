package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.DisableOnClickMode;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ButtonViewTest extends SpringBrowserlessTest {

    @Test
    void until_response_enables_the_button_with_the_next_response() {
        navigate(ButtonView.class);
        Button save = $(Button.class).withTestId("until-response").single();
        assertThat(save.getDisableOnClickMode()).isEqualTo(DisableOnClickMode.UNTIL_RESPONSE);

        var enabledDuringClick = new AtomicBoolean(true);
        save.addClickListener(e -> enabledDuringClick.set(save.isEnabled()));

        // The view's listener takes 2 seconds. Browserless runs the
        // before-response callbacks after the click, like a real response.
        test(save).click();

        assertThat(enabledDuringClick).as("disabled while the click is handled").isFalse();
        assertThat(save.isEnabled()).as("enabled again by the response").isTrue();
        assertThat($(Span.class).testId("response-count").getText()).isEqualTo("1 save");
    }

    @Test
    void until_enabled_keeps_the_button_disabled() {
        navigate(ButtonView.class);
        Button save = $(Button.class).testId("until-enabled");

        test(save).click();
        roundTrip();

        assertThat(save.isEnabled()).isFalse();
    }
}
