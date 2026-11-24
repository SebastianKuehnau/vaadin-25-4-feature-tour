package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("smarter-grid")
@PageTitle("Smarter Grid")
@Menu(title = "Smarter Grid", parent = ComponentsArea.class, order = 6)
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

    public GridView() {
        super("Smarter Grid",
                "Multi-select by clicking anywhere on a row, not just the checkbox. Find a column by its sort property, "
                        + "and build context menus that know which column was clicked.",
                "https://vaadin.com/docs/next/components/grid");

        addRowClickDemo();
        addSortPropertyDemo();
    }

    /** Multi-select grid where a click anywhere on a row toggles it. */
    static class RowClickSelectGrid extends Grid<Feature> {
        RowClickSelectGrid() {
            super(Feature.class, false);
            addColumn(Feature::name).setHeader("Feature");
            addColumn(Feature::area).setHeader("Area");
            setItems(FEATURES);
            setAllRowsVisible(true);
            var model = (GridMultiSelectionModel<Feature>) setSelectionMode(SelectionMode.MULTI);
            model.setSelectOnItemClick(true);
            setTestId("select-grid");
        }
    }

    private void addRowClickDemo() {
        var grid = new RowClickSelectGrid();
        var selected = new StatusText("selected", "0 selected");
        grid.addSelectionListener(e -> selected.setText(e.getAllSelectedItems().size() + " selected"));

        addDemo("Multi-select with a click on the row",
                "Click anywhere on a row to toggle it, not only on the checkbox.",
                new VerticalLayout(selected, grid), """
                        class RowClickSelectGrid extends Grid<Feature> {
                            RowClickSelectGrid() {
                                super(Feature.class, false);
                                addColumn(Feature::name).setHeader("Feature");
                                addColumn(Feature::area).setHeader("Area");
                                var model = (GridMultiSelectionModel<Feature>) setSelectionMode(SelectionMode.MULTI);
                                model.**setSelectOnItemClick(true)**;
                            }
                        }
                        """);
    }

    /** Sortable columns, and a context menu built for the clicked column. */
    static class SortableGrid extends Grid<Feature> {
        SortableGrid(StatusText lastAction) {
            super(Feature.class, false);
            addColumn(Feature::name).setHeader("Feature").setSortProperty("name").setKey("name");
            addColumn(Feature::area).setHeader("Area").setSortProperty("area").setKey("area");
            addColumn(Feature::tier).setHeader("Tier").setSortProperty("tier").setKey("tier");
            setItems(FEATURES);
            setAllRowsVisible(true);
            setTestId("sort-grid");

            GridContextMenu<Feature> menu = addContextMenu();
            menu.setDynamicContentProvider(context -> {
                menu.removeAll();
                if (context.item() == null || context.column() == null) {
                    return false;
                }
                String header = context.column().getHeaderText();
                menu.addItem("Filter by " + header + " of " + context.item().name(),
                        e -> lastAction.setText("Would filter by " + header));
                menu.addItem("Hide column " + header, e -> {
                    context.column().setVisible(false);
                    Notification.show("Column " + header + " hidden");
                });
                return true;
            });
        }
    }

    /** Sorts a grid by the column found for a sort property, as if it came from a ?sort= URL parameter. */
    static class SortPropertySelect extends Select<String> {
        SortPropertySelect(Grid<Feature> grid, StatusText lastAction) {
            super("Sort by property (e.g. from a ?sort= URL parameter)", e -> {
                Grid.Column<Feature> column = grid.getColumnBySortProperty(e.getValue());
                grid.sort(GridSortOrder.asc(column).build());
                lastAction.setText("getColumnBySortProperty(\"" + e.getValue() + "\") → column \""
                        + column.getHeaderText() + "\"");
            }, "name", "area", "tier");
            setWidth("22rem");
            setTestId("sort-by");
        }
    }

    private void addSortPropertyDemo() {
        var lastAction = new StatusText("last-action", "Right-click a cell.");
        var grid = new SortableGrid(lastAction);

        addDemo("Sort property lookup and column-aware context menu",
                "Pick a sort property to sort through the column found for it. Right-click a cell: the menu is built for "
                        + "the clicked column, from DynamicContentContext.",
                new VerticalLayout(new SortPropertySelect(grid, lastAction), grid, lastAction), """
                        class SortPropertySelect extends Select<String> {
                            SortPropertySelect(Grid<Feature> grid) {
                                super("Sort by property", e -> {
                                    Grid.Column<Feature> column = grid.**getColumnBySortProperty(e.getValue())**;
                                    grid.sort(GridSortOrder.asc(column).build());
                                }, "name", "area", "tier");
                            }
                        }

                        class SortableGrid extends Grid<Feature> {
                            SortableGrid() {
                                ...
                                GridContextMenu<Feature> menu = addContextMenu();
                                menu.setDynamicContentProvider(context -> {
                                    menu.removeAll();
                                    if (**context.column()** == null) return false;
                                    menu.addItem("Hide column " + **context.column()**.getHeaderText(),
                                            e -> context.column().setVisible(false));
                                    return true;
                                });
                            }
                        }
                        """);
    }
}
