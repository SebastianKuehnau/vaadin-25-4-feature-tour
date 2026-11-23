package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
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
    final Span status = new Span();

    public DownloadsView() {
        super("Downloads Everywhere",
                Messages.get("flow.downloads.lead"),
                "https://vaadin.com/docs/next/flow/advanced/downloads");

        status.setText(Messages.get("flow.downloads.status.none"));
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
        // The callback runs on a download request thread, so keep the UI to translate with
        var ui = UI.getCurrent();
        return DownloadHandler.fromInputStream(event -> {
            byte[] content = ("release,feature,tier\n25.4,Strict CSP,Free\n25.4,PDF Viewer,Commercial\n"
                    + "25.4,Downloads Everywhere,Free\n").getBytes(StandardCharsets.UTF_8);
            return new DownloadResponse(new ByteArrayInputStream(content), name, "text/csv", content.length);
        }).whenComplete(ok -> status.setText(ui.getTranslation(
                ok ? "flow.downloads.status.served" : "flow.downloads.status.failed", LocalTime.now().format(TIME), name)));
    }

    private void addCardDemo() {
        reportCard.setTitle(Messages.get("flow.downloads.card.title"));
        reportCard.setSubtitle(Messages.get("flow.downloads.card.subtitle"));
        reportCard.setHeaderPrefix(VaadinIcon.FILE_TABLE.create());
        reportCard.addClassName("download-card");
        reportCard.setTestId("report-card");
        Download.onClick(reportCard, csv("features-25-4.csv"));

        addDemo(Messages.get("flow.downloads.card.heading"),
                Messages.get("flow.downloads.card.desc"),
                reportCard, """
                        Download.onClick(reportCard, DownloadHandler.fromInputStream(event ->
                                        new DownloadResponse(stream, "features-25-4.csv", "text/csv", length))
                                .whenComplete(ok -> status.setText(ok ? "Served" : "Failed")));
                        """);
    }

    private void addNewTabDemo() {
        var poster = new Button(Messages.get("flow.downloads.newtab.poster"), VaadinIcon.EXTERNAL_LINK.create());
        Download.onClick(poster, DownloadHandler.forClassResource(DownloadsView.class, "/media/poster.jpg"),
                DownloadTarget.NEW_TAB);
        var docs = new Button(Messages.get("flow.downloads.newtab.docs"), VaadinIcon.BOOK.create());
        Download.onClick(docs, "https://vaadin.com/docs/next", DownloadTarget.NEW_TAB);

        var row = new FlexLayout(poster, docs);
        row.addClassName("showcase-row");
        addDemo(Messages.get("flow.downloads.newtab.heading"),
                Messages.get("flow.downloads.newtab.desc"),
                row, """
                        Download.onClick(poster, DownloadHandler.forClassResource(..., "/media/poster.jpg"),
                                DownloadTarget.NEW_TAB);
                        Download.onClick(docs, "https://vaadin.com/docs/next", DownloadTarget.NEW_TAB);
                        """);
        addNote(Messages.get("flow.downloads.newtab.note"), true);
    }

    private void addButtonDemo() {
        var button = new Button(Messages.get("flow.downloads.button.label"), VaadinIcon.DOWNLOAD.create());
        button.setDownloadHandler(csv("button-export.csv"));
        button.setTestId("button-download");
        addDemo("Button.setDownloadHandler()",
                Messages.get("flow.downloads.button.desc"),
                button, """
                        Button export = new Button("Export CSV");
                        export.setDownloadHandler(csvHandler);
                        """);
    }

    private void addMenuDemo() {
        var menuBar = new MenuBar();
        var export = menuBar.addItem(Messages.get("flow.downloads.menu.export")).getSubMenu();
                export.addItem(Messages.get("flow.downloads.menu.csv")).setDownloadHandler(csv("menu-bar.csv"));
        export.addItem(Messages.get("flow.downloads.menu.poster")).setDownloadHandler(
                DownloadHandler.forClassResource(DownloadsView.class, "/media/poster.jpg"));

        var target = new Div(new Span(Messages.get("flow.downloads.menu.target")));
        target.addClassName("context-target");
        var contextMenu = new ContextMenu(target);
        contextMenu.addItem(Messages.get("flow.downloads.menu.report")).setDownloadHandler(csv("context-menu.csv"));
        contextMenu.addItem(Messages.get("flow.downloads.menu.other"),
                e -> status.setText(Messages.get("flow.downloads.menu.regular")));

        var row = new FlexLayout(menuBar, target);
        row.addClassName("showcase-row");
        addDemo(Messages.get("flow.downloads.menu.heading"),
                Messages.get("flow.downloads.menu.desc"),
                row, """
                        MenuBar menuBar = new MenuBar();
                        SubMenu export = menuBar.addItem("Export").getSubMenu();
                        export.addItem("As CSV").setDownloadHandler(csvHandler);

                        ContextMenu contextMenu = new ContextMenu(target);
                        contextMenu.addItem("Download report").setDownloadHandler(csvHandler);
                        """);
    }
}
