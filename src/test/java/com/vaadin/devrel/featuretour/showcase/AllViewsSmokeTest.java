package com.vaadin.devrel.featuretour.showcase;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.devrel.featuretour.showcase.home.HomeView;
import com.vaadin.flow.component.Component;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Opens every showcase page once, so a broken page fails the build.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AllViewsSmokeTest extends SpringBrowserlessTest {

    @ParameterizedTest
    @ValueSource(classes = {HomeView.class})
    void page_opens(Class<? extends Component> view) {
        assertThat(navigate(view)).isInstanceOf(view);
    }
}
