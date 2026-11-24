package com.vaadin.devrel.featuretour.showcase.components;

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

@Route("labels-aside")
@PageTitle("Forms: Labels Aside")
@Menu(title = "Forms: Labels Aside", parent = ComponentsArea.class, order = 2)
@ShowcaseInfo(summary = "LABEL_ASIDE on fields, and label text alignment in Form Layout.", tiers = Tier.FREE)
public class LabelsAsideView extends ShowcasePage {

    final AsideFields fields = new AsideFields();

    public LabelsAsideView() {
        super("Forms: Labels Aside",
                "The LABEL_ASIDE theme variant puts a field's label next to the input instead of above it, on its own, "
                        + "without a Form Layout. Form Layout gets a label text alignment setting for its labels aside mode.",
                "https://vaadin.com/docs/next/components/form-layout");

        addFieldsDemo();
        addFormLayoutDemo();
    }

    /** One field of each kind that supports LABEL_ASIDE, without a Form Layout around them. */
    static class AsideFields extends VerticalLayout {
        final TextField name = new TextField("Name");
        final ComboBox<String> country = new ComboBox<>("Country", "Finland", "Germany", "Spain", "United States");
        final DatePicker start = new DatePicker("Start date");
        final TimePicker time = new TimePicker("Daily stand-up");
        final Select<String> role = new Select<>("Role", e -> { }, "Developer", "Architect", "DevRel");
        final CheckboxGroup<String> topics = new CheckboxGroup<>("Topics", "Flow", "Hilla", "Copilot");
        final RadioButtonGroup<String> level = new RadioButtonGroup<>("Level", "Beginner", "Advanced");
        final IntegerSlider experience = new IntegerSlider("Years of Vaadin", 0, 25);

        AsideFields() {
            add(name, country, start, time, role, topics, level, experience);
            setPadding(false);
            addClassName("aside-fields");
            name.setTestId("aside-name");
            experience.setValue(7);
            setLabelsAside(true);
        }

        void setLabelsAside(boolean on) {
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
    }

    private void addFieldsDemo() {
        var aside = new Switch("Labels aside", true);
        aside.setTestId("labels-aside-switch");
        aside.addValueChangeListener(e -> fields.setLabelsAside(e.getValue()));

        addDemo("One variant for every field",
                "Text fields, combo boxes, pickers, select, checkbox and radio groups and slider all support it. "
                        + "Toggle the switch to compare with labels on top.",
                new VerticalLayout(aside, fields), """
                        class AsideFields extends VerticalLayout {
                            final TextField name = new TextField("Name");
                            final ComboBox<String> country = new ComboBox<>("Country", "Finland", "Germany", "Spain", "United States");
                            ...

                            void setLabelsAside(boolean on) {
                                if (on) {
                                    name.addThemeVariants(**TextFieldVariant.LABEL_ASIDE**);
                                    country.addThemeVariants(**ComboBoxVariant.LABEL_ASIDE**);
                                    start.addThemeVariants(**DatePickerVariant.LABEL_ASIDE**);
                                    time.addThemeVariants(**TimePickerVariant.LABEL_ASIDE**);
                                    role.addThemeVariants(**SelectVariant.LABEL_ASIDE**);
                                    topics.addThemeVariants(**CheckboxGroupVariant.LABEL_ASIDE**);
                                    level.addThemeVariants(**RadioGroupVariant.LABEL_ASIDE**);
                                    experience.addThemeVariants(**SliderVariant.LABEL_ASIDE**);
                                } else {
                                    name.removeThemeVariants(TextFieldVariant.LABEL_ASIDE);
                                    ...
                                }
                            }
                        }
                        """);
    }

    /** A small contact form with its labels aside, aligned to the end. */
    static class ContactForm extends FormLayout {
        ContactForm() {
            setAutoResponsive(true);
            setLabelsAside(true);
            setLabelWidth("10rem");
            setLabelTextAlign(LabelTextAlign.END);
            addFormItem(new TextField(), "First name");
            addFormItem(new TextField(), "Last name");
            addFormItem(new EmailField(), "Email address");
            setTestId("aside-form");
        }
    }

    /** Picks the label text alignment of a Form Layout. */
    static class AlignmentPicker extends RadioButtonGroup<LabelTextAlign> {
        AlignmentPicker(FormLayout form) {
            super("Label text alignment", List.of(LabelTextAlign.START, LabelTextAlign.CENTER, LabelTextAlign.END));
            setItemLabelGenerator(a -> a.name().charAt(0) + a.name().substring(1).toLowerCase());
            setValue(form.getLabelTextAlign());
            addValueChangeListener(e -> form.setLabelTextAlign(e.getValue()));
            setTestId("label-align");
        }
    }

    private void addFormLayoutDemo() {
        var form = new ContactForm();
        addDemo("Label text alignment in Form Layout",
                "When a Form Layout shows its labels aside, the labels can now be aligned to the start, center or end. "
                        + "End alignment keeps short labels close to their fields.",
                new VerticalLayout(new AlignmentPicker(form), form), """
                        class ContactForm extends FormLayout {
                            ContactForm() {
                                setAutoResponsive(true);
                                setLabelsAside(true);
                                setLabelWidth("10rem");
                                **setLabelTextAlign(LabelTextAlign.END)**; // START, CENTER or END
                                addFormItem(new TextField(), "First name");
                                addFormItem(new TextField(), "Last name");
                                addFormItem(new EmailField(), "Email address");
                            }
                        }

                        class AlignmentPicker extends RadioButtonGroup<LabelTextAlign> {
                            AlignmentPicker(FormLayout form) {
                                super("Label text alignment", List.of(LabelTextAlign.START, LabelTextAlign.CENTER, LabelTextAlign.END));
                                setValue(form.**getLabelTextAlign()**);
                                addValueChangeListener(e -> form.**setLabelTextAlign(e.getValue())**);
                            }
                        }
                        """);
    }
}
