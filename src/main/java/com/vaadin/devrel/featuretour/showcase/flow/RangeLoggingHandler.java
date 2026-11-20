package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.streams.DownloadEvent;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.io.IOException;

/**
 * Passes every request on to a handler and reports its Range header, so the
 * media demo can show the byte ranges a player asks for while seeking.
 */
final class RangeLoggingHandler implements DownloadHandler {

    private final DownloadHandler delegate;
    private final SerializableConsumer<String> log;

    RangeLoggingHandler(DownloadHandler delegate, SerializableConsumer<String> log) {
        this.delegate = delegate;
        this.log = log;
    }

    @Override
    public void handleDownloadRequest(DownloadEvent event) throws IOException {
        String range = event.getRequest().getHeader("Range");
        delegate.handleDownloadRequest(event);
        log.accept(range == null ? "whole file" : range);
    }

    @Override
    public String getUrlPostfix() {
        return delegate.getUrlPostfix();
    }

    @Override
    public boolean isRangeRequestsEnabled() {
        // Without this, the wrapper would turn off range requests
        return delegate.isRangeRequestsEnabled();
    }
}
