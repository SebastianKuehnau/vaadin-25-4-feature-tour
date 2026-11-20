package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class LifecycleViewTest extends SpringBrowserlessTest {

    @Test
    void dashboard_shows_navigations_after_a_round_trip() throws InterruptedException {
        navigate(LifecycleView.class);
        test($(Button.class).withTestId("navigate-step").single()).click();

        // The dashboard refreshes from a timer thread through UI.access(). Wait
        // for the timer to queue its task, then run it like a server round trip.
        Thread.sleep(1500);
        roundTrip();

        String log = $(Div.class).testId("lifecycle-events").getElement().getTextRecursively();
        assertThat(log).contains("Navigation started", "/lifecycle-events?step=1", "UI_NAVIGATE",
                "completed → LifecycleView");
    }

    @Test
    void session_log_survives_a_reload() throws InterruptedException {
        navigate(LifecycleView.class);

        reload(LifecycleView.class);
        Thread.sleep(1500);
        roundTrip();

        // The recorder keeps the log per session, so the navigation from before
        // the reload is still there, next to the one the reload caused
        String log = $(Div.class).testId("lifecycle-events").getElement().getTextRecursively();
        assertThat(log.split("completed → LifecycleView", -1)).hasSize(3);
        assertThat(log).contains("UI_NAVIGATE", "PROGRAMMATIC");
    }
}
