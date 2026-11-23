package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
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
    final Switch busy = new Switch();

    public AiGridView(AiSupport ai, SalesDatabase database) {
        super(ai, "AI Grid Paging",
                Messages.get("ai.grid.lead"),
                "https://vaadin.com/docs/next/flow/ai-support/ai-powered-grid",
                Tier.COMMERCIAL, Tier.PREVIEW);

        addGridDemo(ai, database);
        addSubmitDisabledDemo();
    }

    private void addGridDemo(AiSupport ai, SalesDatabase database) {
        grid.setHeight("22rem");
        grid.setEmptyStateText(Messages.get("ai.grid.empty"));
        grid.setTestId("ai-grid");
        input.setWidthFull();
        input.setTestId("ai-input");
        status.addClassName("showcase-status");
        pagingLog.addClassName("csp-warnings");
        pagingLog.setTestId("paging-log");
        pagingLog.add(new Span(Messages.get("ai.grid.logHint")));

        if (ai.isConfigured()) {
            UI ui = UI.getCurrentOrThrow();
            String thinking = Messages.get("ai.grid.thinking");
            String done = Messages.get("ai.grid.done");
            var provider = new PagingDatabaseProvider(database.readOnlyDataSource(), line -> ui.access(() -> log(line)));
            AIOrchestrator.builder(ai.newProvider(), SYSTEM_PROMPT)
                    .withInput(input)
                    .withController(new GridAIController(grid, provider))
                    .withRequestListener(event -> status.setText(thinking))
                    .withResponseListener(event -> ui.access(() -> status.setText(done)))
                    .build();
        } else {
            input.setEnabled(false);
        }

        addDemo(Messages.get("ai.grid.demo1.heading"),
                Messages.get("ai.grid.demo1.desc"),
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
        busy.setLabel(Messages.get("ai.grid.busy"));
        busy.setTestId("busy");
        busy.addValueChangeListener(e -> manualInput.setSubmitDisabled(e.getValue()));
        addDemo(Messages.get("ai.grid.demo2.heading"),
                Messages.get("ai.grid.demo2.desc"),
                new VerticalLayout(busy, manualInput), """
                        messageInput.setSubmitDisabled(true);  // typing still works
                        messageInput.setSubmitDisabled(false);
                        """);
    }
}
