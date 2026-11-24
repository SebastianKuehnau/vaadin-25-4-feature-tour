package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatTile;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.devrel.featuretour.showcase.home.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Table;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.js.JsDefinition;
import com.vaadin.flow.js.JsExpression;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.local.ValueSignal;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Route("lifecycle-events")
@PageTitle("Lifecycle Events")
@Menu(title = "Lifecycle Events", parent = FlowArea.class, order = 4)
@ShowcaseInfo(summary = "Request and navigation start/end events, plus client-side round-trip events.", tiers = Tier.FREE)
public class LifecycleView extends ShowcasePage implements BeforeLeaveObserver {

    /** Follows the client engine's requests in the browser, without a server round trip. */
    @JsDefinition
    public interface RoundTripJs extends Serializable {
        @JsExpression("""
                const out = this;
                for (const client of Object.values(window.Vaadin?.Flow?.clients ?? {})) {
                  if (!client.events) continue;
                  client.__tourOut = out;
                  if (client.__tourHooked) continue;
                  client.__tourHooked = true;
                  const stats = { requests: 0, total: 0, messages: 0, last: '' };
                  const show = () => client.__tourOut.textContent = 'Browser: ' + stats.requests + ' requests, avg round trip '
                      + (stats.requests ? (stats.total / stats.requests).toFixed(1) : '0') + ' ms, last ' + stats.last
                      + ' · ' + stats.messages + ' server messages (replies and pushes)';
                  client.events.addEventListener('vaadin-request', ({ detail: request }) => {
                    let sentAt;
                    request.addEventListener('sent', () => sentAt = performance.now());
                    request.addEventListener('end', () => {
                      const ms = performance.now() - (sentAt ?? performance.now());
                      stats.requests++; stats.total += ms;
                      stats.last = ms.toFixed(1) + ' ms (' + request.outcome + ', attempt ' + request.attempt + ')';
                      show();
                    });
                  });
                  client.events.addEventListener('vaadin-server-message', () => { stats.messages++; show(); });
                }
                """)
        void track();
    }

    private final LifecycleRecorder recorder;
    final ValueSignal<LifecycleRecorder.Stats> stats = new ValueSignal<>(LifecycleRecorder.Stats.EMPTY);
    final EventTable events = new EventTable();
    final StatusText browser = new StatusText("round-trip", "Browser: waiting for the first request…");
    final Checkbox postpone = new Checkbox("Postpone the next navigation away from this page");
    private int step;

    public LifecycleView(LifecycleRecorder recorder) {
        super("Lifecycle Events",
                "The service event bus from 25.3 now also reports the start and end of every request and navigation, with "
                        + "outcome, duration and handler. In the browser, the client engine fires events for each request "
                        + "and server message. Observability Kit uses the same events.",
                "https://vaadin.com/docs/next/flow/advanced/session-lock-and-rpc-listeners");
        this.recorder = recorder;

        addDashboard();
        addPlayground();
        addBrowserDemo();
        whenAttached(this::startRefreshing);
    }

    private Registration startRefreshing(UI ui) {
        refresh(ui);
        var timer = new Timer("lifecycle-dashboard", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ui.access(() -> refresh(ui));
            }
        }, 1000, 1000);
        browser.getElement().executeJs(RoundTripJs.class).track();
        return timer::cancel;
    }

    void refresh(UI ui) {
        var current = recorder.get(ui.getSession().getSession());
        stats.set(current.stats());
        events.show(current.recent());
    }

    /**
     * The latest events of the session. A plain table instead of a Grid: a Grid
     * acknowledges each update with a request of its own, which would show up
     * here and refresh again.
     */
    static class EventTable extends Table {
        private String shownFirst = "";

        EventTable() {
            addClassName("lifecycle-log");
            setTestId("lifecycle-events");
            addHeaderRow("Time", "Event", "Handler / location", "Outcome", "Duration");
        }

        void show(List<LifecycleRecorder.Entry> recent) {
            String first = recent.isEmpty() ? "" : recent.getFirst().toString();
            if (first.equals(shownFirst)) {
                return;
            }
            shownFirst = first;
            getBody().removeAll();
            recent.stream().limit(25).forEach(e -> addRow(e.time(), e.type(), e.detail(), e.outcome(),
                    e.millis() == 0 ? "" : String.format("%.2f ms", e.millis())));
        }
    }

    /** Request and navigation counters, refreshed from a signal. */
    static class StatTiles extends FlexLayout {
        StatTiles(ValueSignal<LifecycleRecorder.Stats> stats) {
            add(new StatTile("Requests started / ended",
                            () -> stats.get().requestsStarted() + " / " + stats.get().requestsEnded()),
                    new StatTile("Avg. request", () -> String.format("%.2f ms", stats.get().avgRequestMs())),
                    new StatTile("Navigations", () -> String.valueOf(stats.get().navigations())),
                    new StatTile("Avg. navigation", () -> String.format("%.2f ms", stats.get().avgNavigationMs())),
                    new StatTile("RPC invocations", () -> String.valueOf(stats.get().rpcCalls())));
            addClassName("stat-tiles");
        }
    }

    private void addDashboard() {
        var scroller = new Scroller(events);
        scroller.setMaxHeight("22rem");
        scroller.setWidthFull();
        addDemo("Your session, live",
                "A VaadinServiceInitListener records the events of every session from startup, so this dashboard also "
                        + "shows navigations that left this page. The first request of a page load starts before there is a session, "
                        + "so it is counted when it ends but not when it starts.",
                new VerticalLayout(new StatTiles(stats), scroller), """
                        @Component
                        public class LifecycleRecorder implements VaadinServiceInitListener {
                            public void serviceInit(ServiceInitEvent event) {
                                var bus = event.getSource().getEventBus();
                                bus.addListener(**RequestEndedEvent**.class, e -> record(
                                        e.getHandler(), e.getDuration(), e.getFailure()));
                                bus.addListener(**NavigationStartedEvent**.class, e -> started(e.getUI()));
                                bus.addListener(**NavigationEndedEvent**.class, e -> record(
                                        switch (e.getOutcome()) {
                                            case Completed c -> "completed → " + c.navigationTarget().getSimpleName();
                                            case Postponed p -> "postponed";
                                            case Failed f -> "failed: " + f.error();
                                            case NotShown n -> "not shown";
                                        }, e.getStatusCode()));
                            }
                        }
                        """);
    }

    private void addPlayground() {
        var sameRoute = new Button("Navigate with ?step=n", e -> UI.getCurrentOrThrow()
                .navigate(LifecycleView.class, QueryParameters.of("step", String.valueOf(++step))));
        sameRoute.setTestId("navigate-step");
        var broken = new Button("Navigate to a failing route", e -> UI.getCurrentOrThrow().navigate(BrokenView.class));
        var home = new RouterLink("Go to Home (come back with Back)", HomeView.class);
        postpone.setTestId("postpone");

        var row = new FlexLayout(sameRoute, broken, home);
        row.addClassName("showcase-row");
        addDemo("Generate some navigations",
                "Each kind of outcome: completed, failed (the error view is shown), and postponed when the checkbox is on. "
                        + "Come back to this page after leaving it to see the events that happened meanwhile.",
                new VerticalLayout(row, postpone), "");
    }

    private void addBrowserDemo() {
        addDemo("Round trips, measured in the browser",
                "window.Vaadin.Flow.clients[appId].events fires vaadin-request and vaadin-server-message. The script is a "
                        + "@JsDefinition, and it writes into the page directly: reporting each round trip to the server "
                        + "would cause another round trip.",
                browser, """
                        client.events.addEventListener(**'vaadin-request'**, ({ detail: request }) => {
                          let sentAt;
                          request.addEventListener('sent', () => sentAt = performance.now());
                          request.addEventListener('end', () =>
                              show(performance.now() - sentAt, request.outcome)); // acknowledged | rejected | discarded
                        });
                        client.events.addEventListener(**'vaadin-server-message'**, () => messages++);
                        """);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (postpone.getValue() && event.getNavigationTarget() != LifecycleView.class) {
            postpone.setValue(false);
            event.postpone();
            Notification.show("Navigation postponed. Look for the 'postponed' outcome.");
        }
    }
}
