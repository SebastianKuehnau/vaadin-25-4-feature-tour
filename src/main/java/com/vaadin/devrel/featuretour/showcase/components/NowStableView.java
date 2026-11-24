package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.ExternalLink;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsItem;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("now-stable")
@PageTitle("Now Stable")
@Menu(title = "Now Stable", parent = ComponentsArea.class, order = 7)
@ShowcaseInfo(summary = "Switch and Breadcrumbs are production ready: no feature flags.", tiers = Tier.FREE)
public class NowStableView extends ShowcasePage {

    public NowStableView() {
        super("Now Stable",
                "Switch and Breadcrumbs, introduced in 25.3, are production ready in 25.4 and no longer need a feature "
                        + "flag. This app uses no flag for them: see vaadin-featureflags.properties.",
                "https://vaadin.com/docs/next/components/switch");

        var notifications = new Switch("Email notifications", true);
        notifications.setTestId("stable-switch");
        var manual = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
        manual.add(new BreadcrumbsItem("Home", ""), new BreadcrumbsItem("Components", "components"),
                new BreadcrumbsItem("Now Stable", "now-stable"));

        addDemo("No flag needed",
                "A Switch, and Breadcrumbs in manual mode. The trail in the app header is the router mode, built "
                        + "from the route hierarchy.",
                new VerticalLayout(notifications, manual), """
                        // 25.3: com.vaadin.experimental.switchComponent=true, ...breadcrumbsComponent=true
                        // 25.4: just use them
                        Switch notifications = new Switch("Email notifications", true);
                        Breadcrumbs trail = new Breadcrumbs(); // router mode, e.g. in your AppLayout navbar
                        """);

        add(new ExternalLink("https://github.com/SebastianKuehnau/vaadin-25-3-feature-tour",
                "The 25.3 feature tour has full pages for both →"));
    }
}
