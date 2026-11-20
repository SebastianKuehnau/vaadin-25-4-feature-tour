package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.devrel.featuretour.base.ui.AreaPage;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("ai")
@PageTitle("AI")
@Menu(title = "AI", order = 4, icon = "vaadin:magic")
public class AiArea extends AreaPage {

    public AiArea() {
        super("AI", "Paging for the AI Grid and a smarter Message Input.");
    }
}
