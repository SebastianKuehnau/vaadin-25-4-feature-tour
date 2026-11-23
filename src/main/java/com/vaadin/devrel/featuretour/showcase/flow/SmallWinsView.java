package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
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

@Route("small-api-wins")
@PageTitle("Small API Wins")
@Menu(title = "Small API Wins", parent = FlowArea.class, order = 6)
@ShowcaseInfo(summary = "Null-safe converters, findAncestor(predicate), browser time zone, URLs with query parameters.", tiers = Tier.FREE)
public class SmallWinsView extends ShowcasePage {

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

    final Person person = new Person();
    final TextField birthDate = new TextField();
    final Span beanState = new Span();
    final Span ancestor = new Span();
    final Span url = new Span();

    public SmallWinsView() {
        super("Small API Wins",
                Messages.get("flow.smallwins.lead"),
                "https://vaadin.com/docs/next/flow/binding-data/components-binder-validation");

        birthDate.setLabel(Messages.get("flow.smallwins.birthDate.label"));
        addConverterDemo();
        addAncestorDemo();
        addTimeZoneDemo();
        addUrlDemo();
    }

    private void addConverterDemo() {
        var binder = new Binder<>(Person.class);
        binder.forField(birthDate)
                .withNullSafeConverter(LocalDate::parse, LocalDate::toString, e -> Messages.get("flow.smallwins.birthDate.error"), "")
                .bind(Person::getBirthDate, Person::setBirthDate);
        binder.setBean(person);
        birthDate.setTestId("birth-date");
        beanState.setTestId("bean-state");
        beanState.addClassName("showcase-status");
        showBean();
        binder.addValueChangeListener(e -> showBean());

        addDemo(Messages.get("flow.smallwins.converter.heading"),
                Messages.get("flow.smallwins.converter.desc"),
                new VerticalLayout(birthDate, beanState), """
                        binder.forField(birthDate)
                                .withNullSafeConverter(LocalDate::parse, LocalDate::toString,
                                        e -> "Use the format 2026-11-20", "")
                                .bind(Person::getBirthDate, Person::setBirthDate);

                        // Or wrap an existing converter:
                        Converter.nullSafeConverter(new StringToIntegerConverter("Not a number"), "");
                        """);
    }

    private void showBean() {
        beanState.setText("person.getBirthDate() = " + person.getBirthDate());
    }

    private void addAncestorDemo() {
        var button = new Button(Messages.get("flow.smallwins.ancestor.button"));
        button.setTestId("find-ancestor");
        ancestor.setTestId("ancestor");
        ancestor.addClassName("showcase-status");
        button.addClickListener(e -> ancestor.setText(button
                .findAncestor(c -> c.getElement().hasAttribute("data-area"))
                .map(c -> Messages.get("flow.smallwins.ancestor.found", c.getElement().getAttribute("data-area"),
                        c.getClass().getSimpleName()))
                .orElse(Messages.get("flow.smallwins.ancestor.none"))));

        var inner = new Div(new FlexLayout(button));
        inner.getElement().setAttribute("data-area", "inner panel");
        inner.addClassName("ancestor-box");
        var outer = new Div(new Span(Messages.get("flow.smallwins.ancestor.outer")), inner);
        outer.getElement().setAttribute("data-area", "outer panel");
        outer.addClassName("ancestor-box");

        addDemo("Component.findAncestor(predicate)",
                Messages.get("flow.smallwins.ancestor.desc"),
                new VerticalLayout(outer, ancestor), """
                        Optional<Component> panel = button.findAncestor(
                                c -> c.getElement().hasAttribute("data-area"));
                        """);
    }

    private void addTimeZoneDemo() {
        ExtendedClientDetails details = UI.getCurrentOrThrow().getPage().getExtendedClientDetails();
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        var zone = new Span(Messages.get("flow.smallwins.tz.text", String.valueOf(details.getZoneId()),
                details.getBrowserDateTime().format(format), ZoneId.systemDefault().toString()));
        zone.addClassName("showcase-status");
        zone.setTestId("time-zone");
        addDemo(Messages.get("flow.smallwins.tz.heading"),
                Messages.get("flow.smallwins.tz.desc"),
                zone, """
                        ExtendedClientDetails details = UI.getCurrent().getPage().getExtendedClientDetails();
                        ZoneId zone = details.getZoneId();
                        ZonedDateTime now = details.getBrowserDateTime();
                        """);
    }

    private void addUrlDemo() {
        String link = RouteConfiguration.forSessionScope().getUrl(LifecycleView.class, RouteParameters.empty(),
                QueryParameters.of("step", "42"));
        url.setText(link);
        url.setTestId("route-url");
        var anchor = new Anchor(link, Messages.get("flow.smallwins.url.anchor"));
        var row = new FlexLayout(new Span("getUrl(...) = "), url, anchor);
        row.addClassName("showcase-row");
        addDemo("RouteConfiguration.getUrl() with query parameters",
                Messages.get("flow.smallwins.url.desc"),
                row, """
                        String url = RouteConfiguration.forSessionScope().getUrl(LifecycleView.class,
                                RouteParameters.empty(), QueryParameters.of("step", "42"));
                        // lifecycle-events?step=42
                        """);
    }
}
