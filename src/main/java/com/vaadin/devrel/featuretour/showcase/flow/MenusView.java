package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.devrel.featuretour.showcase.security.SecurityArea;
import com.vaadin.flow.component.html.Code;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

import java.util.List;

@Route("hierarchical-menus")
@PageTitle("Hierarchical Menus")
@Menu(title = "Hierarchical Menus", parent = FlowArea.class, order = 3)
@ShowcaseInfo(summary = "@Menu(parent) and getMenuEntriesTree() build this app's own side nav.", tiers = Tier.FREE)
public class MenusView extends ShowcasePage {

    final UnorderedList tree = new UnorderedList();

    public MenusView() {
        super("Hierarchical Menus",
                "MenuConfiguration.getMenuEntriesTree() returns the menu as a tree instead of a flat list. "
                        + "The side nav of this app is built from it: every page names its area with @Menu(parent = ...).",
                "https://vaadin.com/docs/next/flow/routing/menu");

        addTreeDemo();
        addParentDemo();
    }

    private void addTreeDemo() {
        tree.addClassName("menu-tree");
        tree.setTestId("menu-tree");
        render(MenuConfiguration.getMenuEntriesTree(), tree);

        int flat = MenuConfiguration.getMenuEntries().size();
        int roots = MenuConfiguration.getMenuEntriesTree().size();
        var summary = new Span(flat + " menu entries in getMenuEntries(), " + roots + " roots in getMenuEntriesTree().");
        summary.addClassName("showcase-status");

        addDemo("The menu tree of this app",
                "This is what getMenuEntriesTree() returns right now. The drawer on the left renders the same tree "
                        + "recursively into SideNavItems, so a new page shows up in the right group by itself.",
                new VerticalLayout(summary, tree), """
                        // MainLayout: one SideNavItem per entry, children nested
                        for (MenuEntry entry : MenuConfiguration.getMenuEntriesTree()) {
                            nav.addItem(toItem(entry));
                        }

                        static SideNavItem toItem(MenuEntry entry) {
                            var item = new SideNavItem(entry.title(), entry.path());
                            for (MenuEntry child : entry.children()) {
                                item.addItem(toItem(child));
                            }
                            return item;
                        }
                        """);
    }

    private void render(List<MenuEntry> entries, UnorderedList list) {
        for (MenuEntry entry : entries) {
            var item = new ListItem(new Span(entry.title()), new Code("/" + entry.path().replaceFirst("^/", "")));
            if (!entry.children().isEmpty()) {
                var nested = new UnorderedList();
                render(entry.children(), nested);
                item.add(nested);
            }
            list.add(item);
        }
    }

    private void addParentDemo() {
        var security = MenuConfiguration.getMenuEntriesTree().stream()
                .filter(e -> e.menuClass() == SecurityArea.class)
                .findFirst()
                .map(e -> e.children().size() + " pages are nested under " + e.title() + ".")
                .orElse("Security area not found.");
        var status = new Span(security);
        status.addClassName("showcase-status");

        addDemo("Nesting with @Menu(parent)",
                "The pages here have flat URLs such as /strict-csp, so the route hierarchy would put them all at the "
                        + "top level. @Menu(parent = ...) moves them under their area in the menu only: URLs, navigation "
                        + "and the flat getMenuEntries() list stay as they are. Without it, entries nest by @RouteParent "
                        + "or by URL prefix.",
                status, """
                        @Route("security")
                        @Menu(title = "Security", order = 1, icon = "vaadin:shield")
                        public class SecurityArea extends AreaPage { ... }

                        @Route("strict-csp")
                        @Menu(title = "Strict CSP", parent = SecurityArea.class, order = 1)
                        public class StrictCspView extends ShowcasePage { ... }

                        // The parent must be a route that is itself in the menu
                        """);
    }
}
