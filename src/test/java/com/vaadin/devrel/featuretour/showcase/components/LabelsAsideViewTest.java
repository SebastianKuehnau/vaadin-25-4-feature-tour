package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.LabelTextAlign;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class LabelsAsideViewTest extends SpringBrowserlessTest {

    private static List<HasTheme> fields(LabelsAsideView view) {
        return List.of(view.name, view.country, view.start, view.time, view.role, view.topics, view.level,
                view.experience);
    }

    @Test
    void every_field_has_the_label_aside_variant() {
        var view = navigate(LabelsAsideView.class);
        assertThat(fields(view)).allSatisfy(field -> assertThat(field.getThemeNames()).contains("label-aside"));
    }

    @Test
    void switch_puts_labels_back_on_top() {
        var view = navigate(LabelsAsideView.class);

        test($(Switch.class).testId("labels-aside-switch")).switchOff();

        assertThat(fields(view)).allSatisfy(field -> assertThat(field.getThemeNames()).doesNotContain("label-aside"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void label_text_alignment_follows_the_radio_group() {
        navigate(LabelsAsideView.class);
        FormLayout form = $(FormLayout.class).testId("aside-form");
        assertThat(form.getLabelTextAlign()).isEqualTo(LabelTextAlign.END);

        RadioButtonGroup<LabelTextAlign> align = $(RadioButtonGroup.class).testId("label-align");
        test(align).selectItem("Center");

        assertThat(form.getLabelTextAlign()).isEqualTo(LabelTextAlign.CENTER);
    }
}
