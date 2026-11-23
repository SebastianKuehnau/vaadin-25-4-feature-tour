package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("grid")
@PageTitle("Grid")
@Menu(title = "Grid", parent = ComponentsArea.class, order = 6)
@ShowcaseInfo(summary = "Row click in multi-select, getColumnBySortProperty(), column-aware context menus.", tiers = Tier.FREE)
public class GridView extends ShowcasePage {

    public record Feature(String name, String area, String tier, int pageOrder) {
    }

    static final List<Feature> FEATURES = List.of(
            new Feature("Strict CSP", "Security", "Free", 1),
            new Feature("Declared JavaScript", "Security", "Free", 2),
            new Feature("Media & Native Form", "Flow", "Free", 3),
            new Feature("Downloads Everywhere", "Flow", "Free", 4),
            new Feature("Lifecycle Events", "Flow", "Free", 5),
            new Feature("PDF Viewer", "Components", "Commercial", 6),
            new Feature("Labels Aside", "Components", "Free", 7),
            new Feature("AI Grid Paging", "AI", "Commercial", 8));

    final Grid<Feature> selectGrid = new Grid<>(Feature.class, false);
    final Span selected = new Span(Messages.get("components.grid.selected", 0));
    final Grid<Feature> sortGrid = new Grid<>(Feature.class, false);
    final Span lastAction = new Span(Messages.get("components.grid.lastAction.initial"));

    public GridView() {
        super("Grid",
                Messages.get("components.grid.lead"),
                "https://vaadin.com/docs/next/components/grid");

        addRowClickDemo();
        addSortPropertyDemo();
    }

    private void addRowClickDemo() {
        selectGrid.addColumn(Feature::name).setHeader(Messages.get("components.grid.col.feature"));
        selectGrid.addColumn(Feature::area).setHeader(Messages.get("components.grid.col.area"));
        selectGrid.setItems(FEATURES);
        selectGrid.setAllRowsVisible(true);
        var model = (GridMultiSelectionModel<Feature>) selectGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        model.setSelectOnItemClick(true);
        selectGrid.addSelectionListener(e -> selected.setText(Messages.get("components.grid.selected", e.getAllSelectedItems().size())));
        selectGrid.setTestId("select-grid");
        selected.addClassName("showcase-status");
        selected.setTestId("selected");

        addDemo(Messages.get("components.grid.rowclick.heading"),
                Messages.get("components.grid.rowclick.desc"),
                new VerticalLayout(selected, selectGrid), """
                        var model = (GridMultiSelectionModel<Feature>) grid.setSelectionMode(Grid.SelectionMode.MULTI);
                        model.setSelectOnItemClick(true);
                        """);
    }

    private void addSortPropertyDemo() {
        sortGrid.addColumn(Feature::name).setHeader(Messages.get("components.grid.col.feature")).setSortProperty("name").setKey("name");
        sortGrid.addColumn(Feature::area).setHeader(Messages.get("components.grid.col.area")).setSortProperty("area").setKey("area");
        sortGrid.addColumn(Feature::tier).setHeader(Messages.get("components.grid.col.tier")).setSortProperty("tier").setKey("tier");
        sortGrid.setItems(FEATURES);
        sortGrid.setAllRowsVisible(true);
        sortGrid.setTestId("sort-grid");

        var sortBy = new Select<String>();
        sortBy.setLabel(Messages.get("components.grid.sortBy.label"));
        sortBy.setItems("name", "area", "tier");
        sortBy.setWidth("22rem");
        sortBy.setTestId("sort-by");
        sortBy.addValueChangeListener(e -> {
            Grid.Column<Feature> column = sortGrid.getColumnBySortProperty(e.getValue());
            sortGrid.sort(GridSortOrder.asc(column).build());
            lastAction.setText(Messages.get("components.grid.lastAction.sort", e.getValue(), column.getHeaderText()));
        });

        GridContextMenu<Feature> menu = sortGrid.addContextMenu();
        menu.setDynamicContentProvider(context -> {
            menu.removeAll();
            if (context.item() == null || context.column() == null) {
                return false;
            }
            String header = context.column().getHeaderText();
            menu.addItem(Messages.get("components.grid.menu.filter", header, context.item().name()),
                    e -> lastAction.setText(Messages.get("components.grid.menu.filtering", header)));
            menu.addItem(Messages.get("components.grid.menu.hide", header), e -> {
                context.column().setVisible(false);
                Notification.show(Messages.get("components.grid.menu.hidden", header));
            });
            return true;
        });
        lastAction.addClassName("showcase-status");
        lastAction.setTestId("last-action");

        addDemo(Messages.get("components.grid.sort.heading"),
                Messages.get("components.grid.sort.desc"),
                new VerticalLayout(sortBy, sortGrid, lastAction), """
                        Grid.Column<Feature> column = grid.getColumnBySortProperty("tier");
                        grid.sort(GridSortOrder.asc(column).build());

                        GridContextMenu<Feature> menu = grid.addContextMenu();
                        menu.setDynamicContentProvider(context -> {
                            menu.removeAll();
                            if (context.column() == null) return false;
                            menu.addItem("Hide column " + context.column().getHeaderText(),
                                    e -> context.column().setVisible(false));
                            return true;
                        });
                        """);
    }
}
