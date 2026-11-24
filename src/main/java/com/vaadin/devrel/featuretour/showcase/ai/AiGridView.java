package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.devrel.featuretour.base.ui.LogTable;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.ai.grid.AIDataRow;
import com.vaadin.flow.component.ai.grid.GridAIController;
import com.vaadin.flow.component.ai.orchestrator.AIOrchestrator;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

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

    final Grid<AIDataRow> grid = new ResultGrid();
    final MessageInput input = new MessageInput();
    final LogTable pagingLog = new LogTable("Page requests from the grid appear here. Scroll the grid to load more.", 12);
    final StatusText status = new StatusText("ai-status");

    public AiGridView(AiSupport ai, SalesDatabase database) {
        super(ai, "AI Grid Paging",
                "The AI Grid no longer loads the whole result of the LLM's query. It pages lazily through your "
                        + "DatabaseProvider, so \"show all orders\" works on large tables too.",
                "https://vaadin.com/docs/next/flow/ai-support/ai-powered-grid",
                Tier.COMMERCIAL, Tier.PREVIEW);

        addGridDemo(ai, database);
        addSubmitDisabledDemo();
    }

    /** Shows whatever the LLM's query returns. */
    static class ResultGrid extends Grid<AIDataRow> {
        ResultGrid() {
            setHeight("22rem");
            setEmptyStateText("Ask for example: show all orders from 2026 with the newest first");
            setTestId("ai-grid");
        }
    }

    private void addGridDemo(AiSupport ai, SalesDatabase database) {
        input.setWidthFull();
        input.setTestId("ai-input");
        pagingLog.setTestId("paging-log");

        if (ai.isConfigured()) {
            UI ui = UI.getCurrentOrThrow();
            var provider = new PagingDatabaseProvider(database.readOnlyDataSource(),
                    line -> ui.access(() -> pagingLog.log(line)));
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
                            // **executeQuery(String sql, QueryPage page)** -> ... ORDER BY ... LIMIT ... OFFSET ...
                            // **count(String sql)**                        -> SELECT COUNT(*) FROM (sql)
                        }

                        AIOrchestrator.builder(llmProvider, SYSTEM_PROMPT)
                                .withInput(messageInput) // submit is disabled while a request runs
                                .withController(new **GridAIController(grid, databaseProvider)**)
                                .build();
                        """);
    }

    /** Message input whose submit button a switch disables, while typing still works. */
    static class BusyDemo extends VerticalLayout {
        BusyDemo() {
            var input = new MessageInput();
            input.setWidthFull();
            input.setTestId("manual-input");
            var busy = new Switch("Request running");
            busy.setTestId("busy");
            busy.addValueChangeListener(e -> input.setSubmitDisabled(e.getValue()));
            add(busy, input);
        }
    }

    private void addSubmitDisabledDemo() {
        addDemo("Message Input: submitDisabled",
                "The same API for your own flows, no LLM needed. While submitting is disabled, the user can keep typing "
                        + "the next message, unlike setEnabled(false).",
                new BusyDemo(), """
                        class BusyDemo extends VerticalLayout {
                            BusyDemo() {
                                var input = new MessageInput();
                                var busy = new Switch("Request running");
                                // Typing still works while submitting is disabled
                                busy.addValueChangeListener(e -> input.**setSubmitDisabled(e.getValue())**);
                                add(busy, input);
                            }
                        }
                        """);
    }
}
