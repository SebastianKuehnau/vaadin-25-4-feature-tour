package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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


@Route("pdf-viewer")
@PageTitle("PDF Viewer")
@Menu(title = "PDF Viewer", parent = ComponentsArea.class, order = 1)
@ShowcaseInfo(summary = "Show PDFs in the app: from a URL or a DownloadHandler, with navigation, zoom and toolbar actions.",
        tiers = {Tier.COMMERCIAL, Tier.PREVIEW})
public class PdfViewerView extends ShowcasePage {

    record ZoomOption(String label, Zoom zoom) {
    }

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

    /** The cheat sheet, fetched by the browser from a static URL. */
    static class UrlViewer extends PdfViewer {
        UrlViewer() {
            super("pdf/cheat-sheet.pdf");
            setHeaderTitle("Loaded from a URL");
            setHeight("480px");
            setTestId("pdf-url");
        }
    }

    private void addUrlDemo() {
        addDemo("From a URL",
                "The simplest case: a static file served by the app, or any URL the browser can fetch.",
                new UrlViewer(), """
                        class UrlViewer extends PdfViewer {
                            UrlViewer() {
                                **super("pdf/cheat-sheet.pdf")**;
                                setHeaderTitle("Loaded from a URL");
                                setHeight("480px");
                            }
                        }
                        """);
    }

    /** The cheat sheet, served by a DownloadHandler, with a download button and a custom toolbar action. */
    static class HandlerViewer extends PdfViewer {
        HandlerViewer() {
            super(DownloadHandler.forClassResource(PdfViewerView.class, "/META-INF/resources/pdf/cheat-sheet.pdf"));
            setHeaderTitle("Served by a DownloadHandler");
            setFileName("vaadin-25-4-cheat-sheet.pdf");
            setDownloadButtonVisible(true);
            setHeight("520px");
            setTestId("pdf-handler");
            getToolbarActions().add(new BookmarkButton(this));
        }
    }

    /** A custom action in the viewer's own toolbar. */
    static class BookmarkButton extends Button {
        BookmarkButton(PdfViewer viewer) {
            super(VaadinIcon.BOOKMARK_O.create(), e -> Notification.show("Bookmarked page " + viewer.getPage()));
            addThemeVariants(ButtonVariant.TERTIARY);
            setAriaLabel("Bookmark this page");
            setTestId("pdf-bookmark");
        }
    }

    /** Sets the zoom of a viewer. */
    static class ZoomSelect extends Select<ZoomOption> {
        ZoomSelect(PdfViewer viewer) {
            super("Zoom", e -> viewer.setZoom(e.getValue().zoom()),
                    new ZoomOption("Fit page", Zoom.PAGE_FIT), new ZoomOption("Page width", Zoom.PAGE_WIDTH),
                    new ZoomOption("50 %", Zoom.of(0.5)), new ZoomOption("100 %", Zoom.of(1)),
                    new ZoomOption("150 %", Zoom.of(1.5)));
            setItemLabelGenerator(ZoomOption::label);
            setPlaceholder("Default");
            setWidth("10rem");
        }
    }

    /** Page navigation and zoom from the server. The page label follows the viewer's signals. */
    static class PageControls extends FlexLayout {
        PageControls(PdfViewer viewer) {
            var previous = new Button("Previous", VaadinIcon.ARROW_LEFT.create(),
                    e -> viewer.scrollToPage(Math.max(1, viewer.getPage() - 1)));
            var next = new Button("Next", VaadinIcon.ARROW_RIGHT.create(),
                    e -> viewer.scrollToPage(Math.min(viewer.getPageCount(), viewer.getPage() + 1)));
            next.setTestId("pdf-next");
            var page = new StatusText("pdf-page");
            page.bindText(() -> "Page " + viewer.pageSignal().get() + " of " + viewer.pageCountSignal().get());
            add(previous, next, page, new ZoomSelect(viewer));
            addClassName("showcase-row");
        }
    }

    private void addHandlerDemo() {
        var viewer = new HandlerViewer();
        addDemo("From a DownloadHandler, controlled from Java",
                "Page and page count are signals, so the label follows the viewer. The bookmark icon at the end of the "
                        + "viewer's toolbar is a regular Button added with getToolbarActions().",
                new VerticalLayout(new PageControls(viewer), viewer), """
                        class HandlerViewer extends PdfViewer {
                            HandlerViewer() {
                                **super(DownloadHandler.forClassResource(PdfViewerView.class, "/META-INF/resources/pdf/cheat-sheet.pdf"))**;
                                setFileName("vaadin-25-4-cheat-sheet.pdf");
                                setDownloadButtonVisible(true);
                                **getToolbarActions()**.add(new BookmarkButton(this));
                            }
                        }

                        class BookmarkButton extends Button {
                            BookmarkButton(PdfViewer viewer) {
                                super(VaadinIcon.BOOKMARK_O.create(), e -> Notification.show("Bookmarked page " + viewer.**getPage()**));
                                setAriaLabel("Bookmark this page");
                            }
                        }

                        class PageControls extends FlexLayout {
                            PageControls(PdfViewer viewer) {
                                var next = new Button("Next", e -> viewer.**scrollToPage**(viewer.getPage() + 1));
                                var page = new StatusText("pdf-page");
                                page.bindText(() -> "Page " + viewer.**pageSignal()**.get() + " of " + viewer.**pageCountSignal()**.get());
                                add(previous, next, page, new ZoomSelect(viewer));
                            }
                        }

                        class ZoomSelect extends Select<ZoomOption> {
                            ZoomSelect(PdfViewer viewer) {
                                super("Zoom", e -> viewer.**setZoom**(e.getValue().zoom()),
                                        new ZoomOption("Page width", **Zoom.PAGE_WIDTH**), new ZoomOption("150 %", **Zoom.of(1.5)**));
                            }
                        }
                        """);
    }
}
