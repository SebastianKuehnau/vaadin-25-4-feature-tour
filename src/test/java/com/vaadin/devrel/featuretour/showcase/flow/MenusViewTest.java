package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.devrel.featuretour.showcase.security.DeclaredJsView;
import com.vaadin.devrel.featuretour.showcase.security.SecurityArea;
import com.vaadin.devrel.featuretour.showcase.security.StrictCspView;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MenusViewTest extends SpringBrowserlessTest {

    @Test
    void pages_are_nested_under_their_area_by_menu_parent() {
        navigate(MenusView.class);

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
        var before = navigate(MenusView.class);
        var tree = $(UnorderedList.class).withTestId("menu-tree").single();
        assertThat(tree.getElement().getTextRecursively()).contains("Security", "Strict CSP", "Hierarchical Menus");

        // New in 25.4: simulate a browser reload. The view is recreated in the same session.
        var after = reload(MenusView.class);

        assertThat(after).isNotSameAs(before);
        assertThat($(UnorderedList.class).testId("menu-tree").getElement().getTextRecursively())
                .isEqualTo(tree.getElement().getTextRecursively());
    }
}
