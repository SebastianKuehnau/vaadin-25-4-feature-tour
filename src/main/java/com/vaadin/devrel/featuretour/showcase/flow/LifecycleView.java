package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatTile;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.devrel.featuretour.showcase.home.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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
                const texts = { stats: $0, last: $1 };
                const fmt = (t, ...values) => values.reduce((s, v, i) => s.replace('{' + i + '}', v), t);
                for (const client of Object.values(window.Vaadin?.Flow?.clients ?? {})) {
                  if (!client.events) continue;
                  client.__tourOut = out;
                  client.__tourTexts = texts;
                  if (client.__tourHooked) continue;
                  client.__tourHooked = true;
                  const stats = { requests: 0, total: 0, messages: 0, last: '' };
                  const show = () => client.__tourOut.textContent = fmt(client.__tourTexts.stats, stats.requests,
                      stats.requests ? (stats.total / stats.requests).toFixed(1) : '0', stats.last, stats.messages);
                  client.events.addEventListener('vaadin-request', ({ detail: request }) => {
                    let sentAt;
                    request.addEventListener('sent', () => sentAt = performance.now());
                    request.addEventListener('end', () => {
                      const ms = performance.now() - (sentAt ?? performance.now());
                      stats.requests++; stats.total += ms;
                      stats.last = fmt(client.__tourTexts.last, ms.toFixed(1), request.outcome, request.attempt);
                      show();
                    });
                  });
                  client.events.addEventListener('vaadin-server-message', () => { stats.messages++; show(); });
                }
                """)
        void track(String statsTemplate, String lastTemplate);
    }

    private final LifecycleRecorder recorder;
    final ValueSignal<LifecycleRecorder.Stats> stats = new ValueSignal<>(LifecycleRecorder.Stats.EMPTY);
    final Div events = new Div();
    private String shownFirst = "";
    final Span browser = new Span();
    final Checkbox postpone = new Checkbox();
    private int step;

    public LifecycleView(LifecycleRecorder recorder) {
        super("Lifecycle Events",
                Messages.get("flow.lifecycle.lead"),
                "https://vaadin.com/docs/next/flow/advanced/session-lock-and-rpc-listeners");
        this.recorder = recorder;
        browser.setText(Messages.get("flow.lifecycle.browser.waiting"));
        postpone.setLabel(Messages.get("flow.lifecycle.postpone"));

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
        browser.getElement().executeJs(RoundTripJs.class).track(
                ui.getTranslation("flow.lifecycle.browser.stats"), ui.getTranslation("flow.lifecycle.browser.last"));
        return timer::cancel;
    }

    void refresh(UI ui) {
        var current = recorder.get(ui.getSession().getSession());
        stats.set(current.stats());
        var recent = current.recent();
        String first = recent.isEmpty() ? "" : recent.getFirst().toString();
        if (!first.equals(shownFirst)) {
            // Plain DOM instead of a Grid: a Grid acknowledges each update with a
            // request of its own, which would show up here and refresh again
            shownFirst = first;
            events.removeAll();
            events.add(line(Messages.get("flow.lifecycle.col.time"), Messages.get("flow.lifecycle.col.event"),
                    Messages.get("flow.lifecycle.col.handler"), Messages.get("flow.lifecycle.col.outcome"),
                    Messages.get("flow.lifecycle.col.duration")));
            recent.stream().limit(25).forEach(e -> events.add(line(e.time(), e.type(), e.detail(), e.outcome(),
                    e.millis() == 0 ? "" : String.format("%.2f ms", e.millis()))));
        }
    }

    private void addDashboard() {
        var tiles = new FlexLayout(
                new StatTile(Messages.get("flow.lifecycle.tile.requests"), () -> stats.get().requestsStarted() + " / " + stats.get().requestsEnded()),
                new StatTile(Messages.get("flow.lifecycle.tile.avgRequest"), () -> String.format("%.2f ms", stats.get().avgRequestMs())),
                new StatTile(Messages.get("flow.lifecycle.tile.navigations"), () -> String.valueOf(stats.get().navigations())),
                new StatTile(Messages.get("flow.lifecycle.tile.avgNavigation"), () -> String.format("%.2f ms", stats.get().avgNavigationMs())),
                new StatTile(Messages.get("flow.lifecycle.tile.rpc"), () -> String.valueOf(stats.get().rpcCalls())));
        tiles.addClassName("stat-tiles");

        events.addClassName("lifecycle-log");
        events.setTestId("lifecycle-events");

        addDemo(Messages.get("flow.lifecycle.dashboard.heading"),
                Messages.get("flow.lifecycle.dashboard.desc"),
                new VerticalLayout(tiles, events), """
                        @Component
                        public class LifecycleRecorder implements VaadinServiceInitListener {
                            public void serviceInit(ServiceInitEvent event) {
                                var bus = event.getSource().getEventBus();
                                bus.addListener(RequestEndedEvent.class, e -> record(
                                        e.getHandler(), e.getDuration(), e.getFailure()));
                                bus.addListener(NavigationStartedEvent.class, e -> started(e.getUI()));
                                bus.addListener(NavigationEndedEvent.class, e -> record(
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

    private static Div line(String... cells) {
        var line = new Div();
        for (String cell : cells) {
            line.add(new Span(cell));
        }
        line.addClassName("lifecycle-line");
        return line;
    }

    private void addPlayground() {
        var sameRoute = new Button(Messages.get("flow.lifecycle.nav.step"), e -> UI.getCurrentOrThrow()
                .navigate(LifecycleView.class, QueryParameters.of("step", String.valueOf(++step))));
        sameRoute.setTestId("navigate-step");
        var broken = new Button(Messages.get("flow.lifecycle.nav.broken"), e -> UI.getCurrentOrThrow().navigate(BrokenView.class));
        var home = new RouterLink(Messages.get("flow.lifecycle.nav.home"), HomeView.class);
        postpone.setTestId("postpone");

        var row = new FlexLayout(sameRoute, broken, home);
        row.addClassName("showcase-row");
        addDemo(Messages.get("flow.lifecycle.playground.heading"),
                Messages.get("flow.lifecycle.playground.desc"),
                new VerticalLayout(row, postpone), "");
    }

    private void addBrowserDemo() {
        browser.addClassName("showcase-status");
        browser.setTestId("round-trip");
        addDemo(Messages.get("flow.lifecycle.browser.heading"),
                Messages.get("flow.lifecycle.browser.desc"),
                browser, """
                        client.events.addEventListener('vaadin-request', ({ detail: request }) => {
                          let sentAt;
                          request.addEventListener('sent', () => sentAt = performance.now());
                          request.addEventListener('end', () =>
                              show(performance.now() - sentAt, request.outcome)); // acknowledged | rejected | discarded
                        });
                        client.events.addEventListener('vaadin-server-message', () => messages++);
                        """);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (postpone.getValue() && event.getNavigationTarget() != LifecycleView.class) {
            postpone.setValue(false);
            event.postpone();
            Notification.show(Messages.get("flow.lifecycle.postponed.notification"));
        }
    }
}
