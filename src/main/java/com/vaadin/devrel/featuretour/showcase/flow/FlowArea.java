package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.AreaPage;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("flow")
@PageTitle("Flow")
@Menu(title = "Flow", order = 2, icon = "vaadin:cogs")
public class FlowArea extends AreaPage {

    public FlowArea() {
        super("flow");
    }
}
