package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("runtime-es-modules")
@PageTitle("Runtime ES Modules")
@Menu(title = "Runtime ES Modules", parent = FlowArea.class, order = 6)
@ShowcaseInfo(summary = "@JavaScript(type = MODULE) loads a plain ES module at runtime, without Vite.", tiers = Tier.FREE)
@JavaScript(value = "js/tour-clock.js", type = JavaScript.Type.MODULE)
public class RuntimeModulesView extends ShowcasePage {

    /** The custom element defined by the runtime module. */
    @Tag("tour-clock")
    public static class TourClock extends Component {
    }

    public RuntimeModulesView() {
        super("Runtime ES Modules",
                "Load an ES module at runtime as a <script type=\"module\">, without bundling it.",
                "https://vaadin.com/docs/next/flow/advanced/loading-resources");

        var clock = new TourClock();
        clock.setTestId("tour-clock");
        addDemo("@JavaScript(type = MODULE)",
                "The clock is a custom element from js/tour-clock.js, a plain ES module served as a static resource. "
                        + "type = MODULE loads it as <script type=\"module\"> at runtime, so it does not go through Vite. "
                        + "Use @JsModule for files that should be bundled.",
                clock, """
                        @JavaScript(value = "js/tour-clock.js", type = JavaScript.Type.MODULE)
                        public class RuntimeModulesView extends ShowcasePage { ... }

                        @Tag("tour-clock")
                        public static class TourClock extends Component { }

                        // Or programmatically, e.g. from a CDN:
                        UI.getCurrentOrThrow().getPage().addJavaScript(
                                "https://unpkg.com/lodash@4.17.15", LoadMode.EAGER, JavaScript.Type.MODULE);
                        """);
    }
}
