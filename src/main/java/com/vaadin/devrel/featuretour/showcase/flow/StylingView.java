package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("styling")
@PageTitle("Styling")
@Menu(title = "Styling", parent = FlowArea.class, order = 5)
@ShowcaseInfo(summary = "@StyleSheet(layer) for defaults that app styles always override.", tiers = Tier.FREE)
@StyleSheet(value = "styles/tour-defaults.css", layer = "tour-defaults")
public class StylingView extends ShowcasePage {

    public StylingView() {
        super("Styling",
                "Load a style sheet into a CSS cascade layer, so your own styles always win over it.",
                "https://vaadin.com/docs/next/styling/stylesheets");

        addLayerDemo();
    }

    /** A box styled by the layer, optionally with extra class names that app styles target too. */
    static class LayerBox extends Div {
        LayerBox(String text, String... classNames) {
            super(new Span(text));
            addClassName("layer-box");
            addClassNames(classNames);
        }
    }

    /** One box styled only by the layer, and one where a layered rule and an app rule compete. */
    static class LayerDemo extends Div {
        LayerDemo() {
            var conflicting = new LayerBox("The layer says red with selector "
                    + "#layer-demo div.layer-box.app-styled.app-styled. The app says green with .layer-box.app-styled. "
                    + "Green wins.", "app-styled");
            conflicting.setTestId("layer-conflict");
            add(new LayerBox("Styled only by the layer: amber, as the layer says."), conflicting);
            setId("layer-demo");
            addClassName("layer-demo");
        }
    }

    private void addLayerDemo() {
        addDemo("Cascade layers: defaults you can always override",
                "Rules in a layer have lower priority than rules outside of any layer, regardless of specificity. "
                        + "Ideal for a theme or an add-on's default look: app styles override it without a specificity war.",
                new LayerDemo(), """
                        @StyleSheet(value = "styles/tour-defaults.css", **layer = "tour-defaults"**)
                        public class StylingView extends ShowcasePage { ... }

                        /* styles/tour-defaults.css: in the layer, very specific */
                        #layer-demo div.layer-box.app-styled.app-styled { background: #fecaca; }

                        /* styles.css: no layer, less specific, wins */
                        .layer-box.app-styled { background: #dcfce7; }

                        // Also for themes: @StyleSheet(value = Aura.STYLESHEET, **layer = "aura"**)
                        """);
    }
}
