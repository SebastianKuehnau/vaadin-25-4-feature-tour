package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.html.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LifecycleViewTest {

    @Autowired
    BrowserlessApplicationContext app;
    BrowserlessUIContext window;

    @BeforeEach
    void open() {
        window = app.newUser().newWindow();
        window.navigate(LifecycleView.class);
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    /**
     * The dashboard refreshes from a timer thread through UI.access(). Waits
     * for the timer to queue its task, then runs it like a server round trip.
     */
    String eventLog() throws InterruptedException {
        Thread.sleep(1500);
        window.roundTrip();
        return window.find(Table.class).withTestId("lifecycle-events").single().getElement().getTextRecursively();
    }

    @Test
    void dashboard_shows_navigations_after_a_round_trip() throws InterruptedException {
        window.findButton().withTestId("navigate-step").click();

        assertThat(eventLog()).contains("Navigation started", "/lifecycle-events?step=1", "UI_NAVIGATE",
                "completed → LifecycleView");
    }

    @Test
    void session_log_survives_a_reload() throws InterruptedException {
        window.reload(LifecycleView.class);

        // The recorder keeps the log per session, so the navigation from before
        // the reload is still there, next to the one the reload caused
        String log = eventLog();
        assertThat(log.split("completed → LifecycleView", -1)).hasSize(3);
        assertThat(log).contains("UI_NAVIGATE", "PROGRAMMATIC");
    }
}
