package com.vaadin.devrel.featuretour.base.ui;

import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.dom.Element;

import java.util.regex.Pattern;

/**
 * A code snippet. Text between double asterisks, such as {@code **setCapture**},
 * is shown in bold to point out the API the demo is about.
 */
public class CodeBlock extends Pre {

    private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*", Pattern.DOTALL);

    public CodeBlock(String code) {
        addClassName("showcase-code");
        var matcher = BOLD.matcher(code.strip());
        int last = 0;
        String text = code.strip();
        while (matcher.find()) {
            getElement().appendChild(Element.createText(text.substring(last, matcher.start())),
                    new Element("strong").setText(matcher.group(1)));
            last = matcher.end();
        }
        getElement().appendChild(Element.createText(text.substring(last)));
    }
}
