package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.NavigationEndedEvent;
import com.vaadin.flow.router.NavigationEndedEvent.Completed;
import com.vaadin.flow.router.NavigationEndedEvent.Failed;
import com.vaadin.flow.router.NavigationEndedEvent.NotShown;
import com.vaadin.flow.router.NavigationEndedEvent.Postponed;
import com.vaadin.flow.router.NavigationStartedEvent;
import com.vaadin.flow.server.RequestEndedEvent;
import com.vaadin.flow.server.RequestStartedEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServiceEventBus;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.communication.RpcInvocationEndedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Records the request and navigation lifecycle events of every session, the
 * way a monitoring tool would. It listens from service init, so navigations
 * away from the dashboard and back are recorded too.
 */
@Component
public class LifecycleRecorder implements VaadinServiceInitListener {

    public record Entry(String time, String type, String detail, String outcome, double millis) {
    }

    /** An immutable copy of the counters, for binding to signals. */
    public record Stats(long requestsStarted, long requestsEnded, double avgRequestMs, long navigations,
                        double avgNavigationMs, long rpcCalls) {
        public static final Stats EMPTY = new Stats(0, 0, 0, 0, 0, 0);
    }

    /** What one session has seen so far. */
    public static final class SessionLog {
        final ConcurrentLinkedDeque<Entry> recent = new ConcurrentLinkedDeque<>();
        final AtomicLong requestsStarted = new AtomicLong();
        final AtomicLong requestsEnded = new AtomicLong();
        final AtomicLong requestNanos = new AtomicLong();
        final AtomicLong navigations = new AtomicLong();
        final AtomicLong navigationNanos = new AtomicLong();
        final AtomicLong rpcCalls = new AtomicLong();

        public Stats stats() {
            return new Stats(requestsStarted(), requestsEnded(), avgRequestMs(), navigations(), avgNavigationMs(),
                    rpcCalls());
        }

        public List<Entry> recent() {
            return List.copyOf(recent);
        }

        public long requestsStarted() {
            return requestsStarted.get();
        }

        public long requestsEnded() {
            return requestsEnded.get();
        }

        public double avgRequestMs() {
            long n = requestsEnded.get();
            return n == 0 ? 0 : requestNanos.get() / 1e6 / n;
        }

        public long navigations() {
            return navigations.get();
        }

        public double avgNavigationMs() {
            long n = navigations.get();
            return n == 0 ? 0 : navigationNanos.get() / 1e6 / n;
        }

        public long rpcCalls() {
            return rpcCalls.get();
        }

        void add(Entry entry) {
            recent.addFirst(entry);
            while (recent.size() > 60) {
                recent.pollLast();
            }
        }
    }

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final Map<String, SessionLog> logs = new ConcurrentHashMap<>();
    // NavigationEndedEvent has no duration: remember when each UI started navigating
    private final Map<UI, Long> navigationStarts = new ConcurrentHashMap<>();

    @Override
    public void serviceInit(ServiceInitEvent event) {
        VaadinServiceEventBus bus = event.getSource().getEventBus();

        bus.addListener(RequestStartedEvent.class, e -> logFor(e.getRequest())
                .ifPresent(log -> log.requestsStarted.incrementAndGet()));

        bus.addListener(RequestEndedEvent.class, e -> logFor(e.getRequest()).ifPresent(log -> {
            log.requestsEnded.incrementAndGet();
            log.requestNanos.addAndGet(e.getDuration().toNanos());
            String handler = e.getHandler().map(h -> h.getClass().getSimpleName())
                    .or(() -> e.getSessionlessHandler().map(h -> h.getClass().getSimpleName()))
                    .orElse("no handler");
            String type = e.getRequest().getParameter("v-r");
            log.add(new Entry(now(), "Request", handler + (type == null ? "" : " · v-r=" + type),
                    e.getFailure().map(f -> "failed: " + f.getClass().getSimpleName()).orElse("ok"),
                    e.getDuration().toNanos() / 1e6));
        }));

        bus.addListener(NavigationStartedEvent.class, e -> {
            navigationStarts.put(e.getUI(), System.nanoTime());
            logFor(e.getUI()).ifPresent(log -> log.add(new Entry(now(), "Navigation started",
                    "/" + e.getLocation().getPathWithQueryParameters() + " · " + e.getTrigger(), "", 0)));
        });

        bus.addListener(NavigationEndedEvent.class, e -> {
            Long start = navigationStarts.remove(e.getUI());
            long nanos = start == null ? 0 : System.nanoTime() - start;
            String outcome = switch (e.getOutcome()) {
                case Completed completed -> "completed → " + completed.navigationTarget().getSimpleName();
                case Postponed postponed -> "postponed";
                case Failed failed -> "failed: " + failed.error().getClass().getSimpleName();
                case NotShown notShown -> "not shown";
            };
            logFor(e.getUI()).ifPresent(log -> {
                log.navigations.incrementAndGet();
                log.navigationNanos.addAndGet(nanos);
                log.add(new Entry(now(), "Navigation ended", "/" + e.getLocation().getPathWithQueryParameters()
                        + " · HTTP " + e.getStatusCode(), outcome, nanos / 1e6));
            });
        });

        bus.addListener(RpcInvocationEndedEvent.class, e -> logFor(e.getUI())
                .ifPresent(log -> log.rpcCalls.incrementAndGet()));

        event.getSource().addSessionDestroyListener(e -> {
            WrappedSession session = e.getSession().getSession();
            if (session != null) {
                logs.remove(session.getId());
            }
        });
    }

    public SessionLog get(WrappedSession session) {
        return logs.computeIfAbsent(session.getId(), id -> new SessionLog());
    }

    private java.util.Optional<SessionLog> logFor(VaadinRequest request) {
        return java.util.Optional.ofNullable(request.getWrappedSession(false)).map(this::get);
    }

    private java.util.Optional<SessionLog> logFor(UI ui) {
        return java.util.Optional.ofNullable(ui.getSession()).map(s -> s.getSession()).map(this::get);
    }

    private static String now() {
        return LocalTime.now().format(TIME);
    }
}
