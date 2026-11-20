package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.flow.component.ai.common.QueryPage;
import com.vaadin.flow.function.SerializableConsumer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The JDBC provider of the AI demos, reporting the pages and counts that the
 * AI Grid asks for while it loads rows lazily. The paging itself is the
 * default of DatabaseProvider: the SQL is wrapped with ORDER BY, LIMIT and
 * OFFSET.
 */
class PagingDatabaseProvider extends JdbcDatabaseProvider {

    private final SerializableConsumer<String> log;

    PagingDatabaseProvider(DataSource readOnlyDataSource, SerializableConsumer<String> log) {
        super(readOnlyDataSource);
        this.log = log;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql, QueryPage page) {
        List<Map<String, Object>> rows = super.executeQuery(sql, page);
        String sort = page.sortOrders().stream()
                .map(o -> o.getSorted() + " " + o.getDirection())
                .collect(Collectors.joining(", "));
        log.accept("page: offset " + page.offset() + ", limit " + page.limit()
                + (sort.isEmpty() ? "" : ", sorted by " + sort) + " → " + rows.size() + " rows");
        return rows;
    }

    @Override
    public int count(String sql) {
        int count = super.count(sql);
        log.accept("count → " + count + " rows in total");
        return count;
    }
}
