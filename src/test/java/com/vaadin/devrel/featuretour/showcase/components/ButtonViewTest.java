package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.shared.DisableOnClickMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ButtonViewTest {

    @Autowired
    BrowserlessApplicationContext app;
    BrowserlessUIContext window;

    @BeforeEach
    void open() {
        window = app.newUser().newWindow();
        window.navigate(ButtonView.class);
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    @Test
    void until_response_enables_the_button_with_the_next_response() {
        var save = window.findButton().withTestId("until-response");
        var button = save.component();
        assertThat(button.getDisableOnClickMode()).isEqualTo(DisableOnClickMode.UNTIL_RESPONSE);

        var enabledDuringClick = new AtomicBoolean(true);
        button.addClickListener(e -> enabledDuringClick.set(button.isEnabled()));

        // The view's listener takes 2 seconds. Browserless runs the
        // before-response callbacks after the click, like a real response.
        save.click();

        assertThat(enabledDuringClick).as("disabled while the click is handled").isFalse();
        assertThat(button.isEnabled()).as("enabled again by the response").isTrue();
        assertThat(window.findSpan().withTestId("until-response-count").getText()).isEqualTo("1 save");
    }

    @Test
    void until_enabled_keeps_the_button_disabled() {
        var save = window.findButton().withTestId("until-enabled");

        save.click();
        window.roundTrip();

        assertThat(save.component().isEnabled()).isFalse();
    }
}
