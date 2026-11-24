package com.vaadin.devrel.featuretour.showcase;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.devrel.featuretour.showcase.ai.AiArea;
import com.vaadin.devrel.featuretour.showcase.ai.AiGridView;
import com.vaadin.devrel.featuretour.showcase.components.AccessibilityView;
import com.vaadin.devrel.featuretour.showcase.components.ButtonView;
import com.vaadin.devrel.featuretour.showcase.components.ComponentsArea;
import com.vaadin.devrel.featuretour.showcase.components.GridView;
import com.vaadin.devrel.featuretour.showcase.components.LabelsAsideView;
import com.vaadin.devrel.featuretour.showcase.components.MultiSelectView;
import com.vaadin.devrel.featuretour.showcase.components.NowStableView;
import com.vaadin.devrel.featuretour.showcase.components.PdfViewerView;
import com.vaadin.devrel.featuretour.showcase.components.TextSelectionView;
import com.vaadin.devrel.featuretour.showcase.flow.DownloadsView;
import com.vaadin.devrel.featuretour.showcase.flow.FlowArea;
import com.vaadin.devrel.featuretour.showcase.flow.LifecycleView;
import com.vaadin.devrel.featuretour.showcase.flow.MediaView;
import com.vaadin.devrel.featuretour.showcase.flow.MenusView;
import com.vaadin.devrel.featuretour.showcase.flow.SmallWinsView;
import com.vaadin.devrel.featuretour.showcase.flow.RuntimeModulesView;
import com.vaadin.devrel.featuretour.showcase.flow.StylingView;
import com.vaadin.devrel.featuretour.showcase.home.HomeView;
import com.vaadin.devrel.featuretour.showcase.security.DeclaredJsView;
import com.vaadin.devrel.featuretour.showcase.security.SecurityArea;
import com.vaadin.devrel.featuretour.showcase.security.StrictCspView;
import com.vaadin.devrel.featuretour.showcase.tooling.CopilotView;
import com.vaadin.devrel.featuretour.showcase.tooling.ToolingArea;
import com.vaadin.devrel.featuretour.showcase.tooling.UnderTheHoodView;
import com.vaadin.flow.component.Component;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Opens every page once, so a broken page fails the build.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AllViewsSmokeTest extends SpringBrowserlessTest {

    @ParameterizedTest
    @ValueSource(classes = {HomeView.class,
            SecurityArea.class, StrictCspView.class, DeclaredJsView.class,
            FlowArea.class, MediaView.class, DownloadsView.class, MenusView.class, LifecycleView.class,
            StylingView.class, RuntimeModulesView.class, SmallWinsView.class,
            ComponentsArea.class, PdfViewerView.class, LabelsAsideView.class, MultiSelectView.class,
            TextSelectionView.class, ButtonView.class, GridView.class, NowStableView.class, AccessibilityView.class,
            AiArea.class, AiGridView.class,
            ToolingArea.class, CopilotView.class, UnderTheHoodView.class})
    void page_opens(Class<? extends Component> view) {
        assertThat(navigate(view)).isInstanceOf(view);
    }
}
