package com.vaadin.devrel.featuretour.showcase.tooling;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.GuideCards;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import org.springframework.boot.SpringBootVersion;

@Route("under-the-hood")
@PageTitle("Under the Hood")
@Menu(title = "Under the Hood", parent = ToolingArea.class, order = 2)
@ShowcaseInfo(summary = "Hilla TypeScript in Java, leaner Spring dependencies, Node.js 26, Spring Boot 4.2, breaking changes.")
public class UnderTheHoodView extends ShowcasePage {

    public UnderTheHoodView() {
        super("Under the Hood",
                Messages.get("tooling.hood.lead"),
                "https://vaadin.com/docs/v25/upgrading");

        var versions = new Span(Messages.get("tooling.hood.versions", Version.getFullVersion(),
                SpringBootVersion.getVersion(), String.valueOf(Runtime.version().feature())));
        versions.addClassName("showcase-status");
        versions.setTestId("versions");
        add(versions);

        add(new GuideCards()
                .card(Messages.get("tooling.hood.card1.title"),
                        Messages.get("tooling.hood.card1.text"))
                .card(Messages.get("tooling.hood.card2.title"),
                        Messages.get("tooling.hood.card2.text"))
                .card(Messages.get("tooling.hood.card3.title"),
                        Messages.get("tooling.hood.card3.text"))
                .card(Messages.get("tooling.hood.card4.title"),
                        Messages.get("tooling.hood.card4.text"))
                .card(Messages.get("tooling.hood.card5.title"),
                        Messages.get("tooling.hood.card5.text"))
                .card(Messages.get("tooling.hood.card6.title"),
                        Messages.get("tooling.hood.card6.text"),
                        Messages.get("tooling.hood.card6.step1"),
                        Messages.get("tooling.hood.card6.step2"),
                        Messages.get("tooling.hood.card6.step3"),
                        Messages.get("tooling.hood.card6.step4"),
                        Messages.get("tooling.hood.card6.step5")));
    }
}
