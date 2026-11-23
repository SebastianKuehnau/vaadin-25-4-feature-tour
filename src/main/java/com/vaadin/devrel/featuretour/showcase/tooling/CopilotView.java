package com.vaadin.devrel.featuretour.showcase.tooling;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.GuideCards;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("copilot")
@PageTitle("Copilot 25.4")
@Menu(title = "Copilot 25.4", parent = ToolingArea.class, order = 1)
@ShowcaseInfo(summary = "Kotlin views, Browserless tests from the recorder, Component Gallery and more. A guided tour.")
public class CopilotView extends ShowcasePage {

    public CopilotView() {
        super("Copilot 25.4",
                Messages.get("tooling.copilot.lead"),
                "https://vaadin.com/docs/next/tools/copilot");

        add(new GuideCards()
                .card(Messages.get("tooling.copilot.card1.title"),
                        Messages.get("tooling.copilot.card1.text"),
                        Messages.get("tooling.copilot.card1.step1"),
                        Messages.get("tooling.copilot.card1.step2"),
                        Messages.get("tooling.copilot.card1.step3"),
                        Messages.get("tooling.copilot.card1.step4"))
                .card(Messages.get("tooling.copilot.card2.title"),
                        Messages.get("tooling.copilot.card2.text"),
                        Messages.get("tooling.copilot.card2.step1"))
                .card(Messages.get("tooling.copilot.card3.title"),
                        Messages.get("tooling.copilot.card3.text"),
                        Messages.get("tooling.copilot.card3.step1"),
                        Messages.get("tooling.copilot.card3.step2"))
                .card(Messages.get("tooling.copilot.card4.title"),
                        Messages.get("tooling.copilot.card4.text"),
                        Messages.get("tooling.copilot.card4.step1"))
                .card(Messages.get("tooling.copilot.card5.title"),
                        Messages.get("tooling.copilot.card5.text"),
                        Messages.get("tooling.copilot.card5.step1"))
                .card(Messages.get("tooling.copilot.card6.title"),
                        Messages.get("tooling.copilot.card6.text"),
                        Messages.get("tooling.copilot.card6.step1"),
                        Messages.get("tooling.copilot.card6.step2"))
                .card(Messages.get("tooling.copilot.card7.title"),
                        Messages.get("tooling.copilot.card7.text")));
    }
}
