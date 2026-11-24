package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.StatusText;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Route("everyday-api-upgrades")
@PageTitle("Everyday API Upgrades")
@Menu(title = "Everyday API Upgrades", parent = FlowArea.class, order = 7)
@ShowcaseInfo(summary = "Typed browser time zone, null-safe converters, findAncestor(predicate), URLs with query parameters.", tiers = Tier.FREE)
public class EverydayApiView extends ShowcasePage {

    /** A bean with an optional date. */
    public static class Person {
        private LocalDate birthDate;

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }
    }

    public EverydayApiView() {
        super("Everyday API Upgrades",
                "Typed, null-safe APIs for things almost every app does: time zones, optional values, "
                        + "component trees and links. Helper code you used to write yourself is no longer needed.",
                "https://vaadin.com/docs/next/flow/binding-data/components-binder-validation");

        addTimeZoneDemo();
        addConverterDemo();
        addAncestorDemo();
        addUrlDemo();
    }

    private void addTimeZoneDemo() {
        ExtendedClientDetails details = UI.getCurrentOrThrow().getPage().getExtendedClientDetails();
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        var zone = new StatusText("time-zone", "Browser zone: " + details.getZoneId() + " · browser time: "
                + details.getBrowserDateTime().format(format) + " · server zone: " + ZoneId.systemDefault());
        addDemo("The user's time zone as a java.time ZoneId",
                "Show times in the user's own time zone without parsing a time zone id or calculating with offsets. "
                        + "ExtendedClientDetails gives you a ZoneId and the browser's ZonedDateTime, ready for java.time.",
                zone, """
                        ExtendedClientDetails details = UI.getCurrent().getPage().getExtendedClientDetails();
                        ZoneId zone = details.**getZoneId()**;
                        ZonedDateTime now = details.**getBrowserDateTime()**;
                        """);
    }

    /** Edits an optional birth date and shows what ends up in the bean. */
    static class BirthDateForm extends VerticalLayout {
        final Person person = new Person();
        final TextField birthDate = new TextField("Birth date (yyyy-mm-dd)");
        final StatusText beanState = new StatusText("bean-state");

        BirthDateForm() {
            var binder = new Binder<>(Person.class);
            binder.forField(birthDate)
                    .withNullSafeConverter(LocalDate::parse, LocalDate::toString, e -> "Use the format 2026-11-20", "")
                    .bind(Person::getBirthDate, Person::setBirthDate);
            binder.setBean(person);
            binder.addValueChangeListener(e -> showBean());
            birthDate.setTestId("birth-date");
            showBean();
            add(birthDate, beanState);
        }

        private void showBean() {
            beanState.setText("person.getBirthDate() = " + person.getBirthDate());
        }
    }

    private void addConverterDemo() {
        addDemo("Null-safe converters in Binder",
                "Type a date, then clear the field. The empty field becomes null in the bean instead of a parse error, "
                        + "and a null bean value empties the field. The functions never see null or the empty string.",
                new BirthDateForm(), """
                        class BirthDateForm extends VerticalLayout {
                            final Person person = new Person();
                            final TextField birthDate = new TextField("Birth date (yyyy-mm-dd)");

                            BirthDateForm() {
                                var binder = new Binder<>(Person.class);
                                binder.forField(birthDate)
                                        **.withNullSafeConverter(LocalDate::parse, LocalDate::toString,
                                                e -> "Use the format 2026-11-20", "")**
                                        .bind(Person::getBirthDate, Person::setBirthDate);
                                binder.setBean(person);
                                add(birthDate);
                            }
                        }

                        // Or wrap an existing converter:
                        **Converter.nullSafeConverter**(new StringToIntegerConverter("Not a number"), "");
                        """);
    }

    /** A boxed panel that names its area in a data-area attribute. */
    static class AreaPanel extends Div {
        AreaPanel(String area, Component... content) {
            super(content);
            getElement().setAttribute("data-area", area);
            addClassName("ancestor-box");
        }
    }

    /** Reports the nearest ancestor that has a data-area attribute. */
    static class WhereAmIButton extends Button {
        WhereAmIButton(StatusText result) {
            super("Which section am I in?");
            setTestId("find-ancestor");
            addClickListener(e -> result.setText(findAncestor(c -> c.getElement().hasAttribute("data-area"))
                    .map(c -> "Nearest ancestor with data-area: " + c.getElement().getAttribute("data-area")
                            + " (a " + c.getClass().getSimpleName() + ")")
                    .orElse("No such ancestor")));
        }
    }

    private void addAncestorDemo() {
        var ancestor = new StatusText("ancestor");
        var panels = new AreaPanel("outer panel", new Span("Outer panel"),
                new AreaPanel("inner panel", new FlexLayout(new WhereAmIButton(ancestor))));

        addDemo("Component.findAncestor(predicate)",
                "findAncestor(Class) finds by type. The new overload takes any predicate and walks up the parents.",
                new VerticalLayout(panels, ancestor), """
                        class WhereAmIButton extends Button {
                            WhereAmIButton(StatusText result) {
                                super("Which section am I in?");
                                addClickListener(e -> result.setText(
                                        **findAncestor(c -> c.getElement().hasAttribute("data-area"))**
                                                .map(c -> c.getElement().getAttribute("data-area"))
                                                .orElse("No such ancestor")));
                            }
                        }
                        """);
    }

    private void addUrlDemo() {
        String link = RouteConfiguration.forSessionScope().getUrl(LifecycleView.class, RouteParameters.empty(),
                QueryParameters.of("step", "42"));
        var url = new Span(link);
        url.setTestId("route-url");
        var row = new FlexLayout(new Span("getUrl(...) = "), url, new Anchor(link, "Open it"));
        row.addClassName("showcase-row");
        addDemo("RouteConfiguration.getUrl() with query parameters",
                "Build a link to a route including its query string, without concatenating strings yourself.",
                row, """
                        String url = RouteConfiguration.forSessionScope().**getUrl(LifecycleView.class,
                                RouteParameters.empty(), QueryParameters.of("step", "42"))**;
                        // lifecycle-events?step=42
                        """);
    }
}
