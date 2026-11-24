package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.devrel.featuretour.showcase.security.DeclaredJsView;
import com.vaadin.devrel.featuretour.showcase.security.SecurityArea;
import com.vaadin.devrel.featuretour.showcase.security.StrictCspView;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenusViewTest {

    @Autowired
    BrowserlessApplicationContext app;
    BrowserlessUIContext window;

    @BeforeEach
    void openWindow() {
        window = app.newUser().newWindow();
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    String menuTree() {
        return window.findUnorderedList().withTestId("menu-tree").component().getElement().getTextRecursively();
    }

    @Test
    void pages_are_nested_under_their_area_by_menu_parent() {
        window.navigate(MenusView.class);

        MenuEntry security = MenuConfiguration.getMenuEntriesTree().stream()
                .filter(e -> e.menuClass() == SecurityArea.class)
                .findFirst().orElseThrow();
        assertThat(security.children()).extracting(MenuEntry::menuClass)
                .containsExactly(StrictCspView.class, DeclaredJsView.class);

        // Flat URLs: without @Menu(parent) they would be roots
        assertThat(MenuConfiguration.getMenuEntriesTree()).extracting(MenuEntry::menuClass)
                .doesNotContain(StrictCspView.class, MenusView.class);
    }

    @Test
    void tree_is_rendered_again_after_reload() {
        var before = window.navigate(MenusView.class);
        String tree = menuTree();
        assertThat(tree).contains("Security", "Strict CSP", "Hierarchical Menus");

        // New in 25.4: simulate a browser reload. The view is recreated in the same session.
        var after = window.reload(MenusView.class);

        assertThat(after).isNotSameAs(before);
        assertThat(menuTree()).isEqualTo(tree);
    }
}
