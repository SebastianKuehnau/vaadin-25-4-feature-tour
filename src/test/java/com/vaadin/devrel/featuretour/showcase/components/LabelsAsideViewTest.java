package com.vaadin.devrel.featuretour.showcase.components;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.LabelTextAlign;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LabelsAsideViewTest {

    @Autowired
    BrowserlessApplicationContext app;
    BrowserlessUIContext window;
    LabelsAsideView view;

    @BeforeEach
    void open() {
        window = app.newUser().newWindow();
        view = window.navigate(LabelsAsideView.class);
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    List<HasTheme> fields() {
        var f = view.fields;
        return List.of(f.name, f.country, f.start, f.time, f.role, f.topics, f.level, f.experience);
    }

    @Test
    void every_field_has_the_label_aside_variant() {
        assertThat(fields()).allSatisfy(field -> assertThat(field.getThemeNames()).contains("label-aside"));
    }

    @Test
    void switch_puts_labels_back_on_top() {
        window.findSwitch().withTestId("labels-aside-switch").switchOff();

        assertThat(fields()).allSatisfy(field -> assertThat(field.getThemeNames()).doesNotContain("label-aside"));
    }

    @Test
    void label_text_alignment_follows_the_radio_group() {
        FormLayout form = window.find(FormLayout.class).withTestId("aside-form").single();
        assertThat(form.getLabelTextAlign()).isEqualTo(LabelTextAlign.END);

        window.findRadioButtonGroup(LabelTextAlign.class).withTestId("label-align").selectItem("Center");

        assertThat(form.getLabelTextAlign()).isEqualTo(LabelTextAlign.CENTER);
    }
}
