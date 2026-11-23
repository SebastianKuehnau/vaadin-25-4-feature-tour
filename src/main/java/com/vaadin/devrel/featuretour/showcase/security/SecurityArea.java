package com.vaadin.devrel.featuretour.showcase.security;

import com.vaadin.devrel.featuretour.base.ui.AreaPage;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("security")
@PageTitle("Security")
@Menu(title = "Security", order = 1, icon = "vaadin:shield")
public class SecurityArea extends AreaPage {

    public SecurityArea() {
        super("security");
    }
}
