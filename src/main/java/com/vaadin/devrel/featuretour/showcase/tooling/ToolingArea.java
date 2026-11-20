package com.vaadin.devrel.featuretour.showcase.tooling;

import com.vaadin.devrel.featuretour.base.ui.AreaPage;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("tooling")
@PageTitle("Tooling")
@Menu(title = "Tooling", order = 5, icon = "vaadin:tools")
public class ToolingArea extends AreaPage {

    public ToolingArea() {
        super("Tooling", "What's new in Copilot, and the changes under the hood that don't need a demo.");
    }
}
