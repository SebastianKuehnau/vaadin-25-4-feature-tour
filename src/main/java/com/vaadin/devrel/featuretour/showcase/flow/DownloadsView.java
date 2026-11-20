package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.download.Download;
import com.vaadin.flow.component.download.DownloadTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.InputStreamDownloadHandler;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Route("downloads-everywhere")
@PageTitle("Downloads Everywhere")
@Menu(title = "Downloads Everywhere", parent = FlowArea.class, order = 2)
@ShowcaseInfo(summary = "Download.onClick() on any component, new tabs, Button and menu item downloads.", tiers = Tier.FREE)
public class DownloadsView extends ShowcasePage {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    final Card reportCard = new Card();
    final Span status = new Span("Nothing downloaded yet.");

    public DownloadsView() {
        super("Downloads Everywhere",
                "Start a download, or open content in a new tab, from a click on any component. No Anchor around a "
                        + "button anymore: the component keeps its look, keyboard handling and click listeners.",
                "https://vaadin.com/docs/next/flow/advanced/downloads");

        status.addClassName("showcase-status");
        status.setTestId("download-status");
        add(status);

        addCardDemo();
        addNewTabDemo();
        addButtonDemo();
        addMenuDemo();
    }

    /** A CSV file, created when the browser asks for it. */
    InputStreamDownloadHandler csv(String name) {
        return DownloadHandler.fromInputStream(event -> {
            byte[] content = ("release,feature,tier\n25.4,Strict CSP,Free\n25.4,PDF Viewer,Commercial\n"
                    + "25.4,Downloads Everywhere,Free\n").getBytes(StandardCharsets.UTF_8);
            return new DownloadResponse(new ByteArrayInputStream(content), name, "text/csv", content.length);
        }).whenComplete(ok -> status.setText(LocalTime.now().format(TIME) + (ok ? " ✅ Served " : " ❌ Failed ") + name));
    }

    private void addCardDemo() {
        reportCard.setTitle("Release report");
        reportCard.setSubtitle("features-25-4.csv · click anywhere on the card");
        reportCard.setHeaderPrefix(VaadinIcon.FILE_TABLE.create());
        reportCard.addClassName("download-card");
        reportCard.setTestId("report-card");
        Download.onClick(reportCard, csv("features-25-4.csv"));

        addDemo("Any component: a Card",
                "The download starts in the browser's own click handler, so there is no extra round trip. "
                        + "The content is created lazily, and whenComplete reports success or failure on the server.",
                reportCard, """
                        Download.onClick(reportCard, DownloadHandler.fromInputStream(event ->
                                        new DownloadResponse(stream, "features-25-4.csv", "text/csv", length))
                                .whenComplete(ok -> status.setText(ok ? "Served" : "Failed")));
                        """);
    }

    private void addNewTabDemo() {
        var poster = new Button("Open poster in a new tab", VaadinIcon.EXTERNAL_LINK.create());
        Download.onClick(poster, DownloadHandler.forClassResource(DownloadsView.class, "/media/poster.jpg"),
                DownloadTarget.NEW_TAB);
        var docs = new Button("Open the 25.4 docs", VaadinIcon.BOOK.create());
        Download.onClick(docs, "https://vaadin.com/docs/next", DownloadTarget.NEW_TAB);

        var row = new FlexLayout(poster, docs);
        row.addClassName("showcase-row");
        addDemo("Open in a new tab",
                "DownloadTarget.NEW_TAB opens handler content or a URL in a new tab. The tab opens in the click "
                        + "handler, so popup blockers let it through, unlike Page.open() after a round trip.",
                row, """
                        Download.onClick(poster, DownloadHandler.forClassResource(..., "/media/poster.jpg"),
                                DownloadTarget.NEW_TAB);
                        Download.onClick(docs, "https://vaadin.com/docs/next", DownloadTarget.NEW_TAB);
                        """);
        addNote("Beta 2: the release notes mention OpenInNewTab.onClick(), but there is no such public class yet. "
                + "Download.onClick(component, handler, DownloadTarget.NEW_TAB) does the same today.", true);
    }

    private void addButtonDemo() {
        var button = new Button("Export CSV", VaadinIcon.DOWNLOAD.create());
        button.setDownloadHandler(csv("button-export.csv"));
        button.setTestId("button-download");
        addDemo("Button.setDownloadHandler()",
                "The shortcut for the most common case.",
                button, """
                        Button export = new Button("Export CSV");
                        export.setDownloadHandler(csvHandler);
                        """);
    }

    private void addMenuDemo() {
        var menuBar = new MenuBar();
        var export = menuBar.addItem("Export").getSubMenu();
        export.addItem("As CSV").setDownloadHandler(csv("menu-bar.csv"));
        export.addItem("Poster image").setDownloadHandler(
                DownloadHandler.forClassResource(DownloadsView.class, "/media/poster.jpg"));

        var target = new Div(new Span("Right-click here"));
        target.addClassName("context-target");
        var contextMenu = new ContextMenu(target);
        contextMenu.addItem("Download report").setDownloadHandler(csv("context-menu.csv"));
        contextMenu.addItem("Do something else", e -> status.setText("Regular menu items still work."));

        var row = new FlexLayout(menuBar, target);
        row.addClassName("showcase-row");
        addDemo("Context Menu and Menu Bar items",
                "Menu items can start downloads too, from a menu bar or a context menu.",
                row, """
                        MenuBar menuBar = new MenuBar();
                        SubMenu export = menuBar.addItem("Export").getSubMenu();
                        export.addItem("As CSV").setDownloadHandler(csvHandler);

                        ContextMenu contextMenu = new ContextMenu(target);
                        contextMenu.addItem("Download report").setDownloadHandler(csvHandler);
                        """);
    }
}
