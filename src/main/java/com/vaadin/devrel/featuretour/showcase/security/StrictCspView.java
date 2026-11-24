package com.vaadin.devrel.featuretour.showcase.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.vaadin.devrel.featuretour.base.ui.LogTable;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.badge.BadgeVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Table;
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

    final Span target = new Span("Waiting for JavaScript…");
    final StatusText status = new StatusText("csp-status");
    final WarningLog warnings = new WarningLog();

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

    /** The configured CSP mode as a colored badge. */
    static class ModeBadge extends Badge {
        ModeBadge(CspMode mode) {
            super("vaadin.csp=" + mode.name().toLowerCase());
            addThemeVariants(switch (mode) {
                case OFF -> BadgeVariant.CONTRAST;
                case WARN -> BadgeVariant.WARNING;
                case STRICT -> BadgeVariant.SUCCESS;
            });
        }
    }

    /** What each mode does, in development and in production. */
    static class ModeTable extends Table {
        ModeTable() {
            addClassName("csp-table");
            addHeaderRow("Mode", "Development and production", "Production only");
            addRowWithHeader("off", "Default. Nothing is checked.", "No header.");
            addRowWithHeader("warn",
                    "String JavaScript runs, and each call site is logged once with a stack trace to your code.",
                    "Same as dev. No header.");
            addRowWithHeader("strict", "String JavaScript throws CspViolationException, in dev mode too.",
                    "Also sends a nonce-based Content-Security-Policy header.");
        }
    }

    private void addModeDemo() {
        var config = VaadinService.getCurrent().getDeploymentConfiguration();
        var mode = new StatusText("csp-mode", (config.isProductionMode() ? "Production" : "Development")
                + " mode. Restart with VAADIN_CSP=off|warn|strict to switch.");

        addDemo("The current mode",
                "The mode applies to development and production alike. The only difference is the header: in "
                        + "development mode no header is sent, because the dev server and dev tools need eval.",
                new VerticalLayout(new FlexLayout(new ModeBadge(config.getCspMode())), mode, new ModeTable()), """
                        # application.properties
                        **vaadin.csp=warn**

                        CspMode mode = VaadinService.getCurrent()
                                .getDeploymentConfiguration().**getCspMode()**; // OFF, WARN or STRICT
                        """);
    }

    /** Sends JavaScript as a string, which a strict policy blocks. */
    class LegacyButton extends Button {
        LegacyButton() {
            super("Legacy: executeJs(String)");
            setTestId("csp-legacy");
            addClickListener(e -> {
                try {
                    target.getElement().executeJs("this.textContent = $0", "Set by a string, compiled in the browser");
                    status.setText("Ran. In warn mode the first call from this line is logged below.");
                } catch (CspViolationException violation) {
                    status.setText("Blocked: " + violation.getClass().getSimpleName() + " (strict mode)");
                }
            });
        }
    }

    /** Calls JavaScript that was collected into the bundle at build time. */
    class DeclaredButton extends Button {
        DeclaredButton() {
            super("Declared: executeJs(TextJs.class)");
            setTestId("csp-declared");
            addClickListener(e -> {
                target.getElement().executeJs(TextJs.class).setText("Set by declared JavaScript from the bundle");
                status.setText("Ran. Declared JavaScript is never reported: nothing is compiled in the browser.");
            });
        }
    }

    private void addLegacyDemo() {
        target.setTestId("csp-target");
        target.addClassName("csp-target");

        var buttons = new FlexLayout(new LegacyButton(), new DeclaredButton());
        buttons.addClassName("showcase-row");
        addDemo("A legacy call next to its declared replacement",
                "Both buttons set the text below. The first sends JavaScript as a string, which a strict policy blocks. "
                        + "The second calls an interface method whose JavaScript is collected into the bundle at build time. "
                        + "Warnings from the CSP reporter's logger appear below as they are logged.",
                new VerticalLayout(buttons, target, status, warnings), """
                        class LegacyButton extends Button {
                            LegacyButton() {
                                super("Legacy: executeJs(String)");
                                // Compiled from a string in the browser: blocked by a strict policy
                                addClickListener(e -> target.getElement().executeJs("this.textContent = $0", text));
                            }
                        }

                        **@JsDefinition**
                        public interface TextJs extends Serializable {
                            **@JsExpression("this.textContent = $0")**
                            void setText(String text);
                        }

                        class DeclaredButton extends Button {
                            DeclaredButton() {
                                super("Declared: executeJs(TextJs.class)");
                                // Bundled at build time, called by id
                                addClickListener(e -> target.getElement().**executeJs(TextJs.class).setText(text)**);
                            }
                        }
                        """);
    }

    /** Shows what WARN mode logs while it is attached: time, level and the call site in your code. */
    static class WarningLog extends LogTable {
        WarningLog() {
            super("No warnings captured yet.");
            addClassName("csp-warnings");
            whenAttached(this::captureWarnings);
        }

        private Registration captureWarnings(UI ui) {
            var logger = (Logger) LoggerFactory.getLogger(REPORTER);
            var appender = new AppenderBase<ILoggingEvent>() {
                @Override
                protected void append(ILoggingEvent event) {
                    String callSite = event.getThrowableProxy() == null ? "" : Arrays.stream(
                                    event.getThrowableProxy().getStackTraceElementProxyArray())
                            .map(frame -> frame.getStackTraceElement())
                            .filter(frame -> !frame.getClassName().startsWith("com.vaadin.flow."))
                            .findFirst().map(StackTraceElement::toString).orElse("Vaadin internals");
                    ui.access(() -> log("WARN", callSite));
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
}
