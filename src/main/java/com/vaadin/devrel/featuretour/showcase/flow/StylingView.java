package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("styling")
@PageTitle("Styling")
@Menu(title = "Styling", parent = FlowArea.class, order = 5)
@ShowcaseInfo(summary = "@StyleSheet(layer) for overridable defaults, @JavaScript(type = MODULE) at runtime.", tiers = Tier.FREE)
@StyleSheet(value = "styles/tour-defaults.css", layer = "tour-defaults")
@JavaScript(value = "js/tour-clock.js", type = JavaScript.Type.MODULE)
public class StylingView extends ShowcasePage {

    /** The custom element defined by the runtime module. */
    @Tag("tour-clock")
    public static class TourClock extends Component {
    }

    public StylingView() {
        super("Styling",
                Messages.get("flow.styling.lead"),
                "https://vaadin.com/docs/next/styling/stylesheets");

        addLayerDemo();
        addModuleDemo();
    }

    private void addLayerDemo() {
        var layered = new Div(new Span(Messages.get("flow.styling.layer.styled")));
        layered.addClassName("layer-box");
        var conflicting = new Div(new Span(Messages.get("flow.styling.layer.conflict")));
        conflicting.addClassNames("layer-box", "app-styled");
        conflicting.setTestId("layer-conflict");

        var demo = new Div(layered, conflicting);
        demo.setId("layer-demo");
        demo.addClassName("layer-demo");

        addDemo(Messages.get("flow.styling.layer.heading"),
                Messages.get("flow.styling.layer.desc"),
                demo, """
                        @StyleSheet(value = "styles/tour-defaults.css", layer = "tour-defaults")
                        public class StylingView extends ShowcasePage { ... }

                        /* styles/tour-defaults.css: in the layer, very specific */
                        #layer-demo div.layer-box.app-styled.app-styled { background: #fecaca; }

                        /* styles.css: no layer, less specific, wins */
                        .layer-box.app-styled { background: #dcfce7; }

                        // Also for themes: @StyleSheet(value = Aura.STYLESHEET, layer = "aura")
                        """);
    }

    private void addModuleDemo() {
        var clock = new TourClock();
        clock.setTestId("tour-clock");
        addDemo(Messages.get("flow.styling.module.heading"),
                Messages.get("flow.styling.module.desc"),
                clock, """
                        @JavaScript(value = "js/tour-clock.js", type = JavaScript.Type.MODULE)
                        public class StylingView extends ShowcasePage { ... }

                        @Tag("tour-clock")
                        public static class TourClock extends Component { }
                        """);
    }
}
