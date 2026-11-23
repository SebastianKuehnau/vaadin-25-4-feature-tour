package com.vaadin.devrel.featuretour.showcase.security;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
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

    final TextField snippet = new TextField(Messages.get("security.declaredjs.snippet.label"));
    final Button copy = new Button(Messages.get("security.declaredjs.copy"), VaadinIcon.COPY.create());
    final Button viewport = new Button(Messages.get("security.declaredjs.viewport"));
    final Span copyStatus = new Span();
    final TextField command = new TextField(Messages.get("security.declaredjs.command.label"));
    final Div commandLog = new Div();
    int submitted;

    public DeclaredJsView() {
        super("Declared JavaScript",
                Messages.get("security.declaredjs.lead"),
                "https://vaadin.com/docs/next/flow/security/protections/content-security-policy");

        addClipboardDemo();
        addKeyFilterDemo();
        addComparison();
    }

    private void addClipboardDemo() {
        snippet.setValue("VAADIN_CSP=strict ./mvnw");
        snippet.setWidth("22rem");
        copy.setTestId("copy");
        viewport.setTestId("viewport");
        copyStatus.setTestId("copy-status");
        copyStatus.addClassName("showcase-status");

        copy.addClickListener(e -> copy.getElement().executeJs(ClipboardJs.class)
                .copy(snippet.getValue())
                .then(copied -> copyStatus.setText(copied
                        ? Messages.get("security.declaredjs.copy.ok", snippet.getValue())
                        : Messages.get("security.declaredjs.copy.denied"))));
        viewport.addClickListener(e -> viewport.getElement().executeJs(ClipboardJs.class)
                .viewport()
                .then(v -> copyStatus.setText(Messages.get("security.declaredjs.viewport.result",
                        String.valueOf(v.width()), String.valueOf(v.height()), v.language()))));

        var row = new FlexLayout(snippet, copy, viewport);
        row.addClassName("showcase-row");
        addDemo(Messages.get("security.declaredjs.demo1.heading"),
                Messages.get("security.declaredjs.demo1.desc"),
                new VerticalLayout(row, copyStatus), """
                        @JsDefinition
                        public interface ClipboardJs extends Serializable {
                            @JsExpression("return navigator.clipboard.writeText($0).then(() => true, () => false)")
                            JavaScriptResult<Boolean> copy(String text);

                            @JsExpression("return { width: window.innerWidth, height: window.innerHeight, language: navigator.language }")
                            JavaScriptResult<Viewport> viewport();
                        }

                        copy.getElement().executeJs(ClipboardJs.class)
                                .copy(snippet.getValue())
                                .then(copied -> status.setText(copied ? "Copied" : "Denied"));
                        """);
    }

    private void addKeyFilterDemo() {
        command.setPlaceholder(Messages.get("security.declaredjs.command.placeholder"));
        command.setWidth("22rem");
        command.setTestId("command");
        commandLog.setTestId("command-log");
        commandLog.addClassName("csp-warnings");
        commandLog.add(new Span(Messages.get("security.declaredjs.command.hint")));

        command.getElement().addEventListener("keydown", event -> {
                    String value = event.getEventData().get("element.value").asString();
                    if (submitted++ == 0) {
                        commandLog.removeAll();
                    }
                    var line = new Div(new Span("⏎ #" + submitted), new Span(value));
                    line.addClassName("csp-warning");
                    commandLog.addComponentAsFirst(line);
                })
                .setFilterExpression(FILTERS.isKey("Enter"))
                .addEventDataPath("element.value");

        addDemo(Messages.get("security.declaredjs.demo2.heading"),
                Messages.get("security.declaredjs.demo2.desc"),
                new VerticalLayout(command, commandLog), """
                        @JsDefinition
                        public interface KeyFilters extends Serializable {
                            @JsExpression("return event.key === $0 && !event.repeat;")
                            DomEventFilter isKey(String key);
                        }
                        static final KeyFilters FILTERS = JsSnippets.of(KeyFilters.class);

                        command.getElement().addEventListener("keydown", event -> {
                                    String value = event.getEventData().get("element.value").asString();
                                    submit(value);
                                })
                                .setFilterExpression(FILTERS.isKey("Enter"))
                                .addEventDataPath("element.value");
                        """);
    }

    private void addComparison() {
        var compare = new Div(
                column(Messages.get("security.declaredjs.compare.before"), """
                        element.executeJs(
                            "return navigator.clipboard.writeText($0)", text)
                          .then(Boolean.class, ok -> ...);

                        element.addEventListener("keydown", e -> ...)
                          .setFilter("event.key === 'Enter'")
                          .addEventData("element.value");
                        """),
                column(Messages.get("security.declaredjs.compare.after"), """
                        element.executeJs(ClipboardJs.class)
                          .copy(text)
                          .then(ok -> ...);

                        element.addEventListener("keydown", e -> ...)
                          .setFilterExpression(FILTERS.isKey("Enter"))
                          .addEventDataPath("element.value");
                        """));
        compare.addClassName("compare-grid");
        addDemo(Messages.get("security.declaredjs.compare.heading"),
                Messages.get("security.declaredjs.compare.desc"),
                compare, "");
    }

    private static Component column(String title, String code) {
        var pre = new Pre(code.strip());
        pre.addClassName("showcase-code");
        var column = new Div(new H3(title), pre);
        column.addClassName("compare-column");
        return column;
    }
}
