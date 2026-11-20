package com.vaadin.devrel.featuretour.showcase.tooling;

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
                "No live demo here: Copilot is the demo. This app runs in development mode, so open Copilot with the "
                        + "button at the bottom right and try these on the pages of this tour.",
                "https://vaadin.com/docs/next/tools/copilot");

        add(new GuideCards()
                .card("Browserless tests from the UI test recorder",
                        "The recorder now writes Browserless tests in addition to TestBench tests, and records whether "
                                + "components are enabled or disabled.",
                        "Open Copilot, go to Features and enable the UI test recorder (an experimental plugin).",
                        "Start recording, then use a page: try the Button page and click Save (UNTIL_RESPONSE).",
                        "Stop recording and choose Browserless as the output.",
                        "Copy the test to src/test/java and run ./mvnw test.")
                .card("Kotlin views",
                        "Copilot edits Kotlin views as well as Java: alignment, gap, padding and sizing controls, adding "
                                + "components and templates, and the theme editor generate idiomatic Kotlin, including "
                                + "trailing lambdas and safe calls.",
                        "Try it in a Kotlin project: this tour is Java only.")
                .card("Auto-responsive Form Layout",
                        "Copilot picks the right API for auto-responsive FormLayout, without unnecessary addFormRow calls.",
                        "Open Forms: Labels Aside.",
                        "Select the Form Layout at the bottom and change its columns or labels in Copilot.")
                .card("Component Gallery",
                        "The All Components view is now the Component Gallery. It follows your application theme, is "
                                + "easier to navigate, and imports all your custom components into the UI library at once.",
                        "Open Copilot and drag a component from the Component Gallery onto any page.")
                .card("CMD/CTRL drilldown",
                        "Hold CMD (macOS) or CTRL while hovering to drill down and select nested components. Copilot also "
                                + "handles components that your code adds implicitly.",
                        "On the Grid page, hold CMD/CTRL and hover the grid to reach a single column.")
                .card("Optimistic updates (experimental)",
                        "Edits show up immediately, before the server confirms them. Enable it among Copilot's "
                                + "experimental features.",
                        "Enable optimistic updates under Features.",
                        "Change a button text on the Button page and watch it update instantly.")
                .card("New onboarding and sign-in",
                        "A redesigned onboarding flow and sign-in dialog let you sign in through the license checker, and "
                                + "a menu indicator shows when a new version has release notes. CMD + Enter saves comments."));
    }
}
