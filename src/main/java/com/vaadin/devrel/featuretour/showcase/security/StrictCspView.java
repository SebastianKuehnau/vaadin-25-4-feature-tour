package com.vaadin.devrel.featuretour.showcase.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.badge.BadgeVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.js.JsDefinition;
import com.vaadin.flow.js.JsExpression;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.CspMode;
import com.vaadin.flow.server.CspViolationException;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.shared.Registration;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Route("strict-csp")
@PageTitle("Strict CSP")
@Menu(title = "Strict CSP", parent = SecurityArea.class, order = 1)
@ShowcaseInfo(summary = "vaadin.csp=off|warn|strict: find and remove JavaScript sent as strings.", tiers = Tier.FREE)
public class StrictCspView extends ShowcasePage {

    /** The logger that WARN mode writes to. */
    static final String REPORTER = "com.vaadin.flow.internal.CspViolationReporter";

    @JsDefinition
    public interface TextJs extends Serializable {
        @JsExpression("this.textContent = $0")
        void setText(String text);
    }

    final Span mode = new Span();
    final Button legacy = new Button("Legacy: executeJs(String)");
    final Button declared = new Button("Declared: executeJs(TextJs.class)");
    final Span target = new Span("Waiting for JavaScript…");
    final Span status = new Span();
    final Div warnings = new Div();

    public StrictCspView() {
        super("Strict CSP",
                "Flow can now run under a nonce-based Content-Security-Policy without 'unsafe-eval'. "
                        + "Set vaadin.csp=warn to find every call that sends JavaScript as a string, fix them, then switch to strict.",
                "https://vaadin.com/docs/next/flow/security/protections/content-security-policy");

        addModeDemo();
        addLegacyDemo();
        addNote("Beta 2: Vaadin itself still sends string JavaScript in a few places, such as the event filters of "
                + "double and single click listeners and keyboard shortcuts. Those throw in strict mode until they are "
                + "changed, so try strict on a copy of your app first and keep warn for now.", true);
    }

    private void addModeDemo() {
        CspMode current = VaadinService.getCurrent().getDeploymentConfiguration().getCspMode();
        boolean production = VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode();
        var badge = new Badge("vaadin.csp=" + current.name().toLowerCase());
        badge.addThemeVariants(current == CspMode.OFF ? BadgeVariant.CONTRAST : current == CspMode.WARN
                ? BadgeVariant.WARNING : BadgeVariant.SUCCESS);
        mode.setText((production ? "Production" : "Development") + " mode. Restart with VAADIN_CSP=off|warn|strict to switch.");
        mode.addClassName("showcase-status");

        var table = new Div(
                row("off", "Default. Nothing is checked.", "No header."),
                row("warn", "String JavaScript runs, and each call site is logged once with a stack trace to your code.",
                        "Same as dev. No header."),
                row("strict", "String JavaScript throws CspViolationException, in dev mode too.",
                        "Also sends a nonce-based Content-Security-Policy header."));
        table.addClassName("csp-table");
        var header = row("Mode", "Development and production", "Production only");
        header.addClassName("csp-header");
        table.addComponentAsFirst(header);

        mode.setTestId("csp-mode");
        addDemo("The current mode",
                "The mode applies to development and production alike. The only difference is the header: in "
                        + "development mode no header is sent, because the dev server and dev tools need eval.",
                new VerticalLayout(new FlexLayout(badge), mode, table), """
                        # application.properties
                        vaadin.csp=warn

                        CspMode mode = VaadinService.getCurrent()
                                .getDeploymentConfiguration().getCspMode(); // OFF, WARN or STRICT
                        """);
    }

    private static FlexLayout row(com.vaadin.flow.component.Component... components) {
        var row = new FlexLayout(components);
        row.addClassName("showcase-row");
        return row;
    }

    private static Div row(String mode, String everywhere, String production) {
        var row = new Div(new Span(mode), new Span(everywhere), new Span(production));
        row.addClassName("csp-row");
        return row;
    }

    private void addLegacyDemo() {
        legacy.setTestId("csp-legacy");
        declared.setTestId("csp-declared");
        target.setTestId("csp-target");
        target.addClassName("csp-target");
        status.addClassName("showcase-status");
        warnings.addClassName("csp-warnings");
        warnings.add(new Span("No warnings captured yet."));

        legacy.addClickListener(e -> {
            try {
                target.getElement().executeJs("this.textContent = $0", "Set by a string, compiled in the browser");
                status.setText("Ran. In warn mode the first call from this line is logged below.");
            } catch (CspViolationException violation) {
                status.setText("Blocked: " + violation.getClass().getSimpleName() + " (strict mode)");
            }
        });
        declared.addClickListener(e -> {
            target.getElement().executeJs(TextJs.class).setText("Set by declared JavaScript from the bundle");
            status.setText("Ran. Declared JavaScript is never reported: nothing is compiled in the browser.");
        });

        whenAttached(this::captureWarnings);

        addDemo("A legacy call next to its declared replacement",
                "Both buttons set the text below. The first sends JavaScript as a string, which a strict policy blocks. "
                        + "The second calls an interface method whose JavaScript is collected into the bundle at build time. "
                        + "Warnings from the CSP reporter's logger appear below as they are logged.",
                new VerticalLayout(row(legacy, declared), target, status, warnings), """
                        // Legacy: compiled from a string in the browser
                        target.getElement().executeJs("this.textContent = $0", text);

                        // Declared: bundled at build time, called by id
                        @JsDefinition
                        public interface TextJs extends Serializable {
                            @JsExpression("this.textContent = $0")
                            void setText(String text);
                        }
                        target.getElement().executeJs(TextJs.class).setText(text);
                        """);
    }

    /** Shows what WARN mode logs while this page is attached. */
    private Registration captureWarnings(UI ui) {
        var logger = (Logger) LoggerFactory.getLogger(REPORTER);
        var time = DateTimeFormatter.ofPattern("HH:mm:ss");
        var appender = new AppenderBase<ILoggingEvent>() {
            @Override
            protected void append(ILoggingEvent event) {
                String callSite = event.getThrowableProxy() == null ? "" : Arrays.stream(
                                event.getThrowableProxy().getStackTraceElementProxyArray())
                        .map(frame -> frame.getStackTraceElement())
                        .filter(frame -> !frame.getClassName().startsWith("com.vaadin.flow."))
                        .findFirst().map(StackTraceElement::toString).orElse("Vaadin internals");
                ui.access(() -> {
                    if (warnings.getChildren().findFirst().filter(Span.class::isInstance).isPresent()) {
                        warnings.removeAll();
                    }
                    var line = new Div(new Span(LocalTime.now().format(time)), new Span("WARN"), new Span(callSite));
                    line.addClassName("csp-warning");
                    warnings.add(line);
                });
            }
        };
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);
        return () -> {
            logger.detachAppender(appender);
            appender.stop();
        };
    }
}
