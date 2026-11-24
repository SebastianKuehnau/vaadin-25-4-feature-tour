package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.html.Table;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A live log of what a demo did, newest first. Each entry is a table row that
 * starts with the time. The caption explains what will appear until the first
 * entry arrives.
 */
public class LogTable extends Table {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final int maxRows;

    public LogTable(String emptyText) {
        this(emptyText, Integer.MAX_VALUE);
    }

    public LogTable(String emptyText, int maxRows) {
        this.maxRows = maxRows;
        addClassName("log-table");
        setCaptionText(emptyText);
    }

    /** Adds an entry at the top. Call it with the session locked, e.g. inside ui.access(). */
    public void log(String... cells) {
        removeCaption();
        var body = getBody();
        var row = body.insertRow(0);
        row.addDataCell(LocalTime.now().format(TIME));
        row.addDataCells(cells);
        var rows = body.getRows();
        if (rows.size() > maxRows) {
            body.remove(rows.getLast());
        }
    }
}
