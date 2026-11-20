package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.ai.grid.AIDataRow;
import com.vaadin.flow.component.ai.grid.GridAIController;
import com.vaadin.flow.component.ai.orchestrator.AIOrchestrator;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Route("ai-grid-paging")
@PageTitle("AI Grid Paging")
@Menu(title = "AI Grid Paging", parent = AiArea.class, order = 1)
@ShowcaseInfo(summary = "The AI Grid pages through your DatabaseProvider; Message Input blocks submits while busy.",
        tiers = {Tier.COMMERCIAL, Tier.PREVIEW})
public class AiGridView extends AiPage {

    static final String SYSTEM_PROMPT = """
            You help sales managers explore their sales data. Use readable column aliases such as "Revenue (EUR)".
            Round money to whole euros. Don't add a LIMIT unless the user asks for one: the grid pages by itself.
            """;

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    final Grid<AIDataRow> grid = new Grid<>();
    final MessageInput input = new MessageInput();
    final Div pagingLog = new Div();
    final Span status = new Span();
    final MessageInput manualInput = new MessageInput();
    final Switch busy = new Switch("Request running");

    public AiGridView(AiSupport ai, SalesDatabase database) {
        super(ai, "AI Grid Paging",
                "The AI Grid no longer loads the whole result of the LLM's query. It pages lazily through your "
                        + "DatabaseProvider, so \"show all orders\" works on large tables too.",
                "https://vaadin.com/docs/next/flow/ai-support/ai-powered-grid",
                Tier.COMMERCIAL, Tier.PREVIEW);

        addGridDemo(ai, database);
        addSubmitDisabledDemo();
    }

    private void addGridDemo(AiSupport ai, SalesDatabase database) {
        grid.setHeight("22rem");
        grid.setEmptyStateText("Ask for example: show all orders from 2026 with the newest first");
        grid.setTestId("ai-grid");
        input.setWidthFull();
        input.setTestId("ai-input");
        status.addClassName("showcase-status");
        pagingLog.addClassName("csp-warnings");
        pagingLog.setTestId("paging-log");
        pagingLog.add(new Span("Page requests from the grid appear here. Scroll the grid to load more."));

        if (ai.isConfigured()) {
            UI ui = UI.getCurrentOrThrow();
            var provider = new PagingDatabaseProvider(database.readOnlyDataSource(), line -> ui.access(() -> log(line)));
            AIOrchestrator.builder(ai.newProvider(), SYSTEM_PROMPT)
                    .withInput(input)
                    .withController(new GridAIController(grid, provider))
                    .withRequestListener(event -> status.setText("⏳ Thinking… (submit is disabled meanwhile)"))
                    .withResponseListener(event -> ui.access(() -> status.setText("✅ Done")))
                    .build();
        } else {
            input.setEnabled(false);
        }

        addDemo("Lazy loading through your DatabaseProvider",
                "The sales table has 1,500 rows. Ask for all of them and scroll: the grid asks the provider for one page "
                        + "at a time and for the total count. While the LLM works, the orchestrator disables submitting.",
                new VerticalLayout(input, status, grid, pagingLog), """
                        class JdbcDatabaseProvider implements DatabaseProvider {
                            public String getSchema() { ... }
                            public List<Map<String, Object>> executeQuery(String sql) { ... }

                            // New defaults in 25.4, override them if your SQL dialect pages differently:
                            // executeQuery(String sql, QueryPage page) -> ... ORDER BY ... LIMIT ... OFFSET ...
                            // count(String sql)                        -> SELECT COUNT(*) FROM (sql)
                        }

                        AIOrchestrator.builder(llmProvider, SYSTEM_PROMPT)
                                .withInput(messageInput) // submit is disabled while a request runs
                                .withController(new GridAIController(grid, databaseProvider))
                                .build();
                        """);
    }

    private void log(String line) {
        if (pagingLog.getChildren().findFirst().filter(Span.class::isInstance).isPresent()) {
            pagingLog.removeAll();
        }
        var entry = new Div(new Span(LocalTime.now().format(TIME)), new Span(line));
        entry.addClassName("csp-warning");
        pagingLog.addComponentAsFirst(entry);
        while (pagingLog.getComponentCount() > 12) {
            pagingLog.remove(pagingLog.getComponentAt(pagingLog.getComponentCount() - 1));
        }
    }

    private void addSubmitDisabledDemo() {
        manualInput.setWidthFull();
        manualInput.setTestId("manual-input");
        busy.setTestId("busy");
        busy.addValueChangeListener(e -> manualInput.setSubmitDisabled(e.getValue()));
        addDemo("Message Input: submitDisabled",
                "The same API for your own flows, no LLM needed. While submitting is disabled, the user can keep typing "
                        + "the next message, unlike setEnabled(false).",
                new VerticalLayout(busy, manualInput), """
                        messageInput.setSubmitDisabled(true);  // typing still works
                        messageInput.setSubmitDisabled(false);
                        """);
    }
}
