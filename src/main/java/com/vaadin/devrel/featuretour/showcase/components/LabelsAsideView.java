package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.LabelTextAlign;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.component.slider.SliderVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerVariant;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

@Route("labels-aside")
@PageTitle("Forms: Labels Aside")
@Menu(title = "Forms: Labels Aside", parent = ComponentsArea.class, order = 2)
@ShowcaseInfo(summary = "LABEL_ASIDE on fields, and label text alignment in Form Layout.", tiers = Tier.FREE)
public class LabelsAsideView extends ShowcasePage {

    final TextField name = new TextField(Messages.get("components.labels.name"));
    final ComboBox<String> country = new ComboBox<>(Messages.get("components.labels.country"));
    final DatePicker start = new DatePicker(Messages.get("components.labels.start"));
    final TimePicker time = new TimePicker(Messages.get("components.labels.time"));
    final Select<String> role = new Select<>();
    final CheckboxGroup<String> topics = new CheckboxGroup<>(Messages.get("components.labels.topics"));
    final RadioButtonGroup<String> level = new RadioButtonGroup<>(Messages.get("components.labels.level"));
    final IntegerSlider experience = new IntegerSlider(Messages.get("components.labels.experience"), 0, 25);
    final Switch aside = new Switch(Messages.get("components.labels.aside"), true);
    final FormLayout form = new FormLayout();
    final RadioButtonGroup<LabelTextAlign> align = new RadioButtonGroup<>(Messages.get("components.labels.align"));

    public LabelsAsideView() {
        super("Forms: Labels Aside",
                Messages.get("components.labels.lead"),
                "https://vaadin.com/docs/next/components/form-layout");

        addFieldsDemo();
        addFormLayoutDemo();
    }

    private void addFieldsDemo() {
        country.setItems("Finland", "Germany", "Spain", "United States");
        role.setLabel(Messages.get("components.labels.role"));
        role.setItems(Messages.get("components.labels.role.developer"), Messages.get("components.labels.role.architect"), Messages.get("components.labels.role.devrel"));
        topics.setItems("Flow", "Hilla", "Copilot");
        level.setItems(Messages.get("components.labels.level.beginner"), Messages.get("components.labels.level.advanced"));
        experience.setValue(7);
        aside.setTestId("labels-aside-switch");
        name.setTestId("aside-name");

        aside.addValueChangeListener(e -> applyAside(e.getValue()));
        applyAside(true);

        var fields = new VerticalLayout(name, country, start, time, role, topics, level, experience);
        fields.setPadding(false);
        fields.addClassName("aside-fields");
        addDemo(Messages.get("components.labels.fields.heading"),
                Messages.get("components.labels.fields.desc"),
                new VerticalLayout(aside, fields), """
                        name.addThemeVariants(TextFieldVariant.LABEL_ASIDE);
                        country.addThemeVariants(ComboBoxVariant.LABEL_ASIDE);
                        start.addThemeVariants(DatePickerVariant.LABEL_ASIDE);
                        time.addThemeVariants(TimePickerVariant.LABEL_ASIDE);
                        role.addThemeVariants(SelectVariant.LABEL_ASIDE);
                        topics.addThemeVariants(CheckboxGroupVariant.LABEL_ASIDE);
                        level.addThemeVariants(RadioGroupVariant.LABEL_ASIDE);
                        experience.addThemeVariants(SliderVariant.LABEL_ASIDE);
                        """);
    }

    void applyAside(boolean on) {
        if (on) {
            name.addThemeVariants(TextFieldVariant.LABEL_ASIDE);
            country.addThemeVariants(ComboBoxVariant.LABEL_ASIDE);
            start.addThemeVariants(DatePickerVariant.LABEL_ASIDE);
            time.addThemeVariants(TimePickerVariant.LABEL_ASIDE);
            role.addThemeVariants(SelectVariant.LABEL_ASIDE);
            topics.addThemeVariants(CheckboxGroupVariant.LABEL_ASIDE);
            level.addThemeVariants(RadioGroupVariant.LABEL_ASIDE);
            experience.addThemeVariants(SliderVariant.LABEL_ASIDE);
        } else {
            name.removeThemeVariants(TextFieldVariant.LABEL_ASIDE);
            country.removeThemeVariants(ComboBoxVariant.LABEL_ASIDE);
            start.removeThemeVariants(DatePickerVariant.LABEL_ASIDE);
            time.removeThemeVariants(TimePickerVariant.LABEL_ASIDE);
            role.removeThemeVariants(SelectVariant.LABEL_ASIDE);
            topics.removeThemeVariants(CheckboxGroupVariant.LABEL_ASIDE);
            level.removeThemeVariants(RadioGroupVariant.LABEL_ASIDE);
            experience.removeThemeVariants(SliderVariant.LABEL_ASIDE);
        }
    }

    private void addFormLayoutDemo() {
        form.setAutoResponsive(true);
        form.setLabelsAside(true);
        form.setLabelWidth("10rem");
        form.addFormItem(new TextField(), Messages.get("components.labels.form.first"));
        form.addFormItem(new TextField(), Messages.get("components.labels.form.last"));
        form.addFormItem(new EmailField(), Messages.get("components.labels.form.email"));
        form.setLabelTextAlign(LabelTextAlign.END);
        form.setTestId("aside-form");

        align.setItems(List.of(LabelTextAlign.START, LabelTextAlign.CENTER, LabelTextAlign.END));
        align.setItemLabelGenerator(a -> Messages.get("components.labels.align." + a.name().toLowerCase(Locale.ROOT)));
        align.setValue(LabelTextAlign.END);
        align.addValueChangeListener(e -> form.setLabelTextAlign(e.getValue()));
        align.setTestId("label-align");

        addDemo(Messages.get("components.labels.form.heading"),
                Messages.get("components.labels.form.desc"),
                new VerticalLayout(align, form), """
                        form.setAutoResponsive(true);
                        form.setLabelsAside(true);
                        form.addFormItem(new TextField(), "First name");
                        form.setLabelTextAlign(LabelTextAlign.END); // START, CENTER or END
                        """);
    }
}
