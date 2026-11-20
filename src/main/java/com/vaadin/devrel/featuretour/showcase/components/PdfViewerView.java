package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
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
                "Shows PDF documents inside the application, with page navigation, zoom and a toolbar. Tagged PDFs "
                        + "expose their structure to screen readers.",
                "https://vaadin.com/docs/next/components/pdf-viewer");

        addNote("Preview: enabled with com.vaadin.experimental.pdfViewerComponent=true in "
                + "src/main/resources/vaadin-featureflags.properties. A Vaadin license is needed (a trial works).");
        addUrlDemo();
        addHandlerDemo();
    }

    private void addUrlDemo() {
        fromUrl.setHeaderTitle("Loaded from a URL");
        fromUrl.setHeight("480px");
        fromUrl.setTestId("pdf-url");
        addDemo("From a URL",
                "The simplest case: a static file served by the app, or any URL the browser can fetch.",
                fromUrl, """
                        PdfViewer viewer = new PdfViewer("pdf/cheat-sheet.pdf");
                        viewer.setHeight("480px");
                        """);
    }

    private void addHandlerDemo() {
        fromHandler.setHeaderTitle("Served by a DownloadHandler");
        fromHandler.setFileName("vaadin-25-4-cheat-sheet.pdf");
        fromHandler.setDownloadButtonVisible(true);
        fromHandler.setHeight("520px");
        fromHandler.setTestId("pdf-handler");

        // A custom action in the viewer's own toolbar
        var bookmark = new Button(VaadinIcon.BOOKMARK_O.create(),
                e -> Notification.show("Bookmarked page " + fromHandler.getPage()));
        bookmark.addThemeVariants(ButtonVariant.TERTIARY);
        bookmark.setAriaLabel("Bookmark this page");
        bookmark.setTestId("pdf-bookmark");
        fromHandler.getToolbarActions().add(bookmark);

        // Page navigation from the server, with the state as signals
        var previous = new Button("Previous", VaadinIcon.ARROW_LEFT.create(),
                e -> fromHandler.scrollToPage(Math.max(1, fromHandler.getPage() - 1)));
        var next = new Button("Next", VaadinIcon.ARROW_RIGHT.create(),
                e -> fromHandler.scrollToPage(Math.min(fromHandler.getPageCount(), fromHandler.getPage() + 1)));
        next.setTestId("pdf-next");
        var page = new Span();
        page.setTestId("pdf-page");
        page.addClassName("showcase-status");
        page.bindText(() -> "Page " + fromHandler.pageSignal().get() + " of " + fromHandler.pageCountSignal().get());

        var zoom = new Select<ZoomOption>();
        zoom.setLabel("Zoom");
        zoom.setItems(List.of(new ZoomOption("Fit page", Zoom.PAGE_FIT), new ZoomOption("Page width", Zoom.PAGE_WIDTH),
                new ZoomOption("50 %", Zoom.of(0.5)), new ZoomOption("100 %", Zoom.of(1)),
                new ZoomOption("150 %", Zoom.of(1.5))));
        zoom.setItemLabelGenerator(ZoomOption::label);
        zoom.addValueChangeListener(e -> fromHandler.setZoom(e.getValue().zoom()));
        zoom.setPlaceholder("Default");
        zoom.setWidth("10rem");

        var controls = new FlexLayout(previous, next, page, zoom);
        controls.addClassName("showcase-row");
        addDemo("From a DownloadHandler, controlled from Java",
                "Page and page count are signals, so the label follows the viewer. The bookmark icon at the end of the "
                        + "viewer's toolbar is a regular Button added with getToolbarActions().",
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
