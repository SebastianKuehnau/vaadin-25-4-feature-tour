package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.flow.component.ai.common.QueryPage;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The AI Grid pages through the DatabaseProvider defaults. This checks them
 * against the H2 sales table without an LLM.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PagingDatabaseProviderTest {

    @Autowired
    SalesDatabase database;

    @Test
    void pages_and_counts_through_the_default_methods() {
        var log = new ArrayList<String>();
        var provider = new PagingDatabaseProvider(database.readOnlyDataSource(), log::add);
        String sql = "SELECT id, revenue FROM sales";

        assertThat(provider.count(sql)).isEqualTo(1500);
        var page = provider.executeQuery(sql, new QueryPage(50, 25, QuerySortOrder.desc("ID").build()));

        assertThat(page).hasSize(25);
        assertThat(page.getFirst().get("ID")).isEqualTo(1450);
        assertThat(log).containsExactly("count → 1500 rows in total",
                "page: offset 50, limit 25, sorted by ID DESCENDING → 25 rows");
    }
}
