package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.pdfviewer.PdfViewer;
import com.vaadin.flow.component.pdfviewer.Zoom;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.util.List;

@Route("pdf-viewer")
@PageTitle("PDF Viewer")
@Menu(title = "PDF Viewer", parent = ComponentsArea.class, order = 1)
@ShowcaseInfo(summary = "Show PDFs in the app: from a URL or a DownloadHandler, with navigation, zoom and toolbar actions.",
        tiers = {Tier.COMMERCIAL, Tier.PREVIEW})
public class PdfViewerView extends ShowcasePage {

    record ZoomOption(String label, Zoom zoom) {
    }

    final PdfViewer fromUrl = new PdfViewer("pdf/cheat-sheet.pdf");
    final PdfViewer fromHandler = new PdfViewer(
            DownloadHandler.forClassResource(PdfViewerView.class, "/META-INF/resources/pdf/cheat-sheet.pdf"));

    public PdfViewerView() {
        super("PDF Viewer",
                Messages.get("components.pdf.lead"),
                "https://vaadin.com/docs/next/components/pdf-viewer");

        addNote(Messages.get("components.pdf.note"));
        addUrlDemo();
        addHandlerDemo();
    }

    private void addUrlDemo() {
        fromUrl.setHeaderTitle(Messages.get("components.pdf.url.title"));
        fromUrl.setHeight("480px");
        fromUrl.setTestId("pdf-url");
        addDemo(Messages.get("components.pdf.url.heading"),
                Messages.get("components.pdf.url.desc"),
                fromUrl, """
                        PdfViewer viewer = new PdfViewer("pdf/cheat-sheet.pdf");
                        viewer.setHeight("480px");
                        """);
    }

    private void addHandlerDemo() {
        fromHandler.setHeaderTitle(Messages.get("components.pdf.handler.title"));
        fromHandler.setFileName("vaadin-25-4-cheat-sheet.pdf");
        fromHandler.setDownloadButtonVisible(true);
        fromHandler.setHeight("520px");
        fromHandler.setTestId("pdf-handler");

        // A custom action in the viewer's own toolbar
        var bookmark = new Button(VaadinIcon.BOOKMARK_O.create(),
                e -> Notification.show(Messages.get("components.pdf.bookmarked", fromHandler.getPage())));
        bookmark.addThemeVariants(ButtonVariant.TERTIARY);
        bookmark.setAriaLabel(Messages.get("components.pdf.bookmark.aria"));
        bookmark.setTestId("pdf-bookmark");
        fromHandler.getToolbarActions().add(bookmark);

        // Page navigation from the server, with the state as signals
        var previous = new Button(Messages.get("components.pdf.previous"), VaadinIcon.ARROW_LEFT.create(),
                e -> fromHandler.scrollToPage(Math.max(1, fromHandler.getPage() - 1)));
        var next = new Button(Messages.get("components.pdf.next"), VaadinIcon.ARROW_RIGHT.create(),
                e -> fromHandler.scrollToPage(Math.min(fromHandler.getPageCount(), fromHandler.getPage() + 1)));
        next.setTestId("pdf-next");
        var page = new Span();
        page.setTestId("pdf-page");
        page.addClassName("showcase-status");
        var ui = UI.getCurrent();
        page.bindText(() -> ui.getTranslation("components.pdf.page", fromHandler.pageSignal().get(),
                fromHandler.pageCountSignal().get()));

        var zoom = new Select<ZoomOption>();
        zoom.setLabel(Messages.get("components.pdf.zoom.label"));
        zoom.setItems(List.of(new ZoomOption(Messages.get("components.pdf.zoom.fit"), Zoom.PAGE_FIT),
                new ZoomOption(Messages.get("components.pdf.zoom.width"), Zoom.PAGE_WIDTH),
                new ZoomOption("50 %", Zoom.of(0.5)), new ZoomOption("100 %", Zoom.of(1)),
                new ZoomOption("150 %", Zoom.of(1.5))));
        zoom.setItemLabelGenerator(ZoomOption::label);
        zoom.addValueChangeListener(e -> fromHandler.setZoom(e.getValue().zoom()));
        zoom.setPlaceholder(Messages.get("components.pdf.zoom.default"));
        zoom.setWidth("10rem");

        var controls = new FlexLayout(previous, next, page, zoom);
        controls.addClassName("showcase-row");
        addDemo(Messages.get("components.pdf.handler.heading"),
                Messages.get("components.pdf.handler.desc"),
                new VerticalLayout(controls, fromHandler), """
                        PdfViewer viewer = new PdfViewer(DownloadHandler.forClassResource(..., "cheat-sheet.pdf"));
                        viewer.setFileName("vaadin-25-4-cheat-sheet.pdf");
                        viewer.setDownloadButtonVisible(true);

                        viewer.getToolbarActions().add(new Button(VaadinIcon.BOOKMARK_O.create(),
                                e -> Notification.show("Bookmarked page " + viewer.getPage())));

                        next.addClickListener(e -> viewer.scrollToPage(viewer.getPage() + 1));
                        label.bindText(() -> "Page " + viewer.pageSignal().get()
                                + " of " + viewer.pageCountSignal().get());
                        viewer.setZoom(Zoom.PAGE_WIDTH); // or Zoom.of(1.5)
                        """);
    }
}
