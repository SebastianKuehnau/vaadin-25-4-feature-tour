package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.ui.AreaPage;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("components")
@PageTitle("Components")
@Menu(title = "Components", order = 3, icon = "vaadin:cubes")
public class ComponentsArea extends AreaPage {

    public ComponentsArea() {
        super("Components", "The PDF Viewer preview, labels aside, select all, text selection and more improvements to existing components.");
    }
}
