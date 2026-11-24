package com.vaadin.devrel.featuretour.showcase.security;

import com.vaadin.devrel.featuretour.base.ui.CompareColumn;
import com.vaadin.devrel.featuretour.base.ui.LogTable;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.JavaScriptResult;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.DomEventFilter;
import com.vaadin.flow.js.JsDefinition;
import com.vaadin.flow.js.JsExpression;
import com.vaadin.flow.js.JsSnippets;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.Serializable;

@Route("declared-javascript")
@PageTitle("Declared JavaScript")
@Menu(title = "Declared JavaScript", parent = SecurityArea.class, order = 2)
@ShowcaseInfo(summary = "@JsDefinition, typed JavaScriptResult<T>, event data paths and declared filters.", tiers = Tier.FREE)
public class DeclaredJsView extends ShowcasePage {

    /** JavaScript called from the server. Bundled at build time, called by id. */
    @JsDefinition
    public interface ClipboardJs extends Serializable {
        @JsExpression("return navigator.clipboard.writeText($0).then(() => true, () => false)")
        JavaScriptResult<Boolean> copy(String text);

        @JsExpression("return { width: window.innerWidth, height: window.innerHeight, language: navigator.language }")
        JavaScriptResult<Viewport> viewport();
    }

    /** Converted from the JavaScript object with Jackson. */
    public record Viewport(int width, int height, String language) {
    }

    /** Event filters, created on the server with JsSnippets. */
    @JsDefinition
    public interface KeyFilters extends Serializable {
        @JsExpression("return event.key === $0 && !event.repeat;")
        DomEventFilter isKey(String key);
    }

    static final KeyFilters FILTERS = JsSnippets.of(KeyFilters.class);

    final TextField snippet = new TextField("Text to copy", "VAADIN_CSP=strict ./mvnw", "");
    final StatusText copyStatus = new StatusText("copy-status");

    public DeclaredJsView() {
        super("Declared JavaScript",
                "Declare JavaScript in a Java interface with @JsDefinition and @JsExpression. It is collected into the "
                        + "bundle at build time, so the browser never compiles a string, and calls return typed values.",
                "https://vaadin.com/docs/next/flow/security/protections/content-security-policy");

        addClipboardDemo();
        addKeyFilterDemo();
        addComparison();
    }

    /** Copies the snippet and reports the Boolean the Clipboard API resolved to. */
    class CopyButton extends Button {
        CopyButton() {
            super("Copy", VaadinIcon.COPY.create());
            setTestId("copy");
            addClickListener(e -> getElement().executeJs(ClipboardJs.class)
                    .copy(snippet.getValue())
                    .then(copied -> copyStatus.setText(copied
                            ? "✅ Copied \"" + snippet.getValue() + "\" (JavaScriptResult<Boolean> = true)"
                            : "⚠️ The browser denied clipboard access (JavaScriptResult<Boolean> = false)")));
        }
    }

    /** Reads a JavaScript object into a Java record. */
    class ViewportButton extends Button {
        ViewportButton() {
            super("Read viewport");
            setTestId("viewport");
            addClickListener(e -> getElement().executeJs(ClipboardJs.class)
                    .viewport()
                    .then(v -> copyStatus.setText("📐 " + v.width() + " × " + v.height() + " px, language "
                            + v.language() + " (JavaScriptResult<Viewport>, a Java record)")));
        }
    }

    private void addClipboardDemo() {
        snippet.setWidth("22rem");

        var row = new FlexLayout(snippet, new CopyButton(), new ViewportButton());
        row.addClassName("showcase-row");
        addDemo("Typed results: copy to clipboard",
                "The promise from the Clipboard API resolves to a Boolean on the server. A JavaScript object can just as "
                        + "well become a Java record: the value is converted with Jackson.",
                new VerticalLayout(row, copyStatus), """
                        **@JsDefinition**
                        public interface ClipboardJs extends Serializable {
                            **@JsExpression**("return navigator.clipboard.writeText($0).then(() => true, () => false)")
                            **JavaScriptResult<Boolean>** copy(String text);

                            **@JsExpression**("return { width: window.innerWidth, height: window.innerHeight, language: navigator.language }")
                            **JavaScriptResult<Viewport>** viewport();
                        }

                        record Viewport(int width, int height, String language) { }

                        class CopyButton extends Button {
                            CopyButton() {
                                super("Copy", VaadinIcon.COPY.create());
                                addClickListener(e -> getElement().**executeJs(ClipboardJs.class)**
                                        .copy(snippet.getValue())
                                        .then(copied -> copyStatus.setText(copied ? "Copied" : "Denied")));
                            }
                        }
                        """);
    }

    /** The commands that reached the server. */
    static class CommandLog extends LogTable {
        int submitted;

        CommandLog() {
            super("Only Enter reaches the server. Other keys are filtered in the browser.");
            setTestId("command-log");
        }

        void submit(String command) {
            log("⏎ #" + ++submitted, command);
        }
    }

    /** Sends its value to the server on Enter only, filtered in the browser by a declared filter. */
    static class CommandField extends TextField {
        CommandField(CommandLog log) {
            super("Command");
            setPlaceholder("Type and press Enter");
            setWidth("22rem");
            setTestId("command");
            getElement().addEventListener("keydown",
                            event -> log.submit(event.getEventData().get("element.value").asString()))
                    .setFilterExpression(FILTERS.isKey("Enter"))
                    .addEventDataPath("element.value");
        }
    }

    private void addKeyFilterDemo() {
        var log = new CommandLog();
        addDemo("Event data paths and declared filters",
                "The filter is declared once and created with an argument per listener. The value of the field travels "
                        + "with the event as a property path, which the browser reads without compiling anything.",
                new VerticalLayout(new CommandField(log), log), """
                        @JsDefinition
                        public interface KeyFilters extends Serializable {
                            @JsExpression("return event.key === $0 && !event.repeat;")
                            **DomEventFilter** isKey(String key);
                        }
                        static final KeyFilters FILTERS = **JsSnippets.of(KeyFilters.class)**;

                        class CommandField extends TextField {
                            CommandField(CommandLog log) {
                                super("Command");
                                getElement().addEventListener("keydown",
                                                event -> log.submit(event.getEventData().get("element.value").asString()))
                                        **.setFilterExpression(FILTERS.isKey("Enter"))**
                                        **.addEventDataPath("element.value")**;
                            }
                        }
                        """);
    }

    private void addComparison() {
        var compare = new Div(
                CompareColumn.code("Before 25.4 (deprecated, needs 'unsafe-eval')", """
                        element.executeJs(
                            "return navigator.clipboard.writeText($0)", text)
                          .then(Boolean.class, ok -> ...);

                        element.addEventListener("keydown", e -> ...)
                          .setFilter("event.key === 'Enter'")
                          .addEventData("element.value");
                        """),
                CompareColumn.code("25.4 (CSP-safe)", """
                        element.**executeJs(ClipboardJs.class)**
                          .copy(text)
                          .then(ok -> ...);

                        element.addEventListener("keydown", e -> ...)
                          **.setFilterExpression(FILTERS.isKey("Enter"))**
                          **.addEventDataPath("element.value")**;
                        """));
        compare.addClassName("compare-grid");
        addDemo("Old vs. new",
                "The string variants still work, but setFilter(String) and addEventData(String) are deprecated since "
                        + "25.4, and vaadin.csp=warn reports every call site that still uses them.",
                compare, "");
    }
}
