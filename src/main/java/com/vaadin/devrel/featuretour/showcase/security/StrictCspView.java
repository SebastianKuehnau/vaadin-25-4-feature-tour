package com.vaadin.devrel.featuretour.showcase.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.vaadin.devrel.featuretour.base.i18n.Messages;
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
    final Button legacy = new Button(Messages.get("security.strictcsp.legacy.button"));
    final Button declared = new Button(Messages.get("security.strictcsp.declared.button"));
    final Span target = new Span(Messages.get("security.strictcsp.target.waiting"));
    final Span status = new Span();
    final Div warnings = new Div();

    public StrictCspView() {
        super("Strict CSP",
                Messages.get("security.strictcsp.lead"),
                "https://vaadin.com/docs/next/flow/security/protections/content-security-policy");

        addModeDemo();
        addLegacyDemo();
        addNote(Messages.get("security.strictcsp.note"), true);
    }

    private void addModeDemo() {
        CspMode current = VaadinService.getCurrent().getDeploymentConfiguration().getCspMode();
        boolean production = VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode();
        var badge = new Badge("vaadin.csp=" + current.name().toLowerCase());
        badge.addThemeVariants(current == CspMode.OFF ? BadgeVariant.CONTRAST : current == CspMode.WARN
                ? BadgeVariant.WARNING : BadgeVariant.SUCCESS);
        mode.setText(Messages.get(production ? "security.strictcsp.mode.production" : "security.strictcsp.mode.development"));
        mode.addClassName("showcase-status");

        var table = new Div(
                row("off", Messages.get("security.strictcsp.table.off.all"), Messages.get("security.strictcsp.table.off.prod")),
                row("warn", Messages.get("security.strictcsp.table.warn.all"), Messages.get("security.strictcsp.table.warn.prod")),
                row("strict", Messages.get("security.strictcsp.table.strict.all"), Messages.get("security.strictcsp.table.strict.prod")));
        table.addClassName("csp-table");
        var header = row(Messages.get("security.strictcsp.table.mode"), Messages.get("security.strictcsp.table.everywhere"), Messages.get("security.strictcsp.table.production"));
        header.addClassName("csp-header");
        table.addComponentAsFirst(header);

        mode.setTestId("csp-mode");
        addDemo(Messages.get("security.strictcsp.mode.heading"),
                Messages.get("security.strictcsp.mode.desc"),
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
        warnings.add(new Span(Messages.get("security.strictcsp.warnings.none")));

        legacy.addClickListener(e -> {
            try {
                target.getElement().executeJs("this.textContent = $0", Messages.get("security.strictcsp.legacy.set"));
                status.setText(Messages.get("security.strictcsp.legacy.ran"));
            } catch (CspViolationException violation) {
                status.setText(Messages.get("security.strictcsp.legacy.blocked", violation.getClass().getSimpleName()));
            }
        });
        declared.addClickListener(e -> {
            target.getElement().executeJs(TextJs.class).setText(Messages.get("security.strictcsp.declared.set"));
            status.setText(Messages.get("security.strictcsp.declared.ran"));
        });

        whenAttached(this::captureWarnings);

        addDemo(Messages.get("security.strictcsp.legacy.heading"),
                Messages.get("security.strictcsp.legacy.desc"),
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
