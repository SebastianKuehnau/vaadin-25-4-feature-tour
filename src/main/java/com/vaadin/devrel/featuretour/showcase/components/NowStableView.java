package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsItem;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
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
                Messages.get("components.nowstable.lead"),
                "https://vaadin.com/docs/next/components/switch");

        var notifications = new Switch(Messages.get("components.nowstable.switch"), true);
        notifications.setTestId("stable-switch");
        var manual = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
        manual.add(new BreadcrumbsItem(Messages.get("components.nowstable.crumb.home"), ""), new BreadcrumbsItem(Messages.get("components.nowstable.crumb.components"), "components"),
                new BreadcrumbsItem(Messages.get("components.nowstable.crumb.nowstable"), "now-stable"));

        addDemo(Messages.get("components.nowstable.heading"),
                Messages.get("components.nowstable.desc"),
                new VerticalLayout(notifications, manual), """
                        // 25.3: com.vaadin.experimental.switchComponent=true, ...breadcrumbsComponent=true
                        // 25.4: just use them
                        Switch notifications = new Switch("Email notifications", true);
                        Breadcrumbs trail = new Breadcrumbs(); // router mode, e.g. in your AppLayout navbar
                        """);

        var tour = new Anchor("https://github.com/SebastianKuehnau/vaadin-25-3-feature-tour",
                Messages.get("components.nowstable.link"));
        tour.setTarget(AnchorTarget.BLANK);
        add(tour);
    }
}
