package com.vaadin.devrel.featuretour.base.ui;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeBlockTest {

    @Test
    void double_asterisks_become_strong_elements() {
        var code = new CodeBlock("""
                button.**setCapture(UploadCapture.ENVIRONMENT)**;
                """);

        assertThat(code.getElement().getChildren().map(e -> e.isTextNode() ? "#text" : e.getTag()))
                .containsExactly("#text", "strong", "#text");
        assertThat(code.getElement().getChild(1).getText()).isEqualTo("setCapture(UploadCapture.ENVIRONMENT)");
        assertThat(code.getElement().getTextRecursively()).isEqualTo("button.setCapture(UploadCapture.ENVIRONMENT);");
    }

    @Test
    void code_without_markers_is_plain_text() {
        assertThat(new CodeBlock("int x = 2 * 3;").getElement().getTextRecursively()).isEqualTo("int x = 2 * 3;");
    }
}
