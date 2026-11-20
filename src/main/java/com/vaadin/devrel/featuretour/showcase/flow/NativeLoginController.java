package com.vaadin.devrel.featuretour.showcase.flow;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

/**
 * Receives the native form of the media page. A real app would post to the
 * login processing URL of Spring Security instead.
 */
@RestController
class NativeLoginController {

    @PostMapping(value = "/native-login", produces = MediaType.TEXT_HTML_VALUE)
    String login(@RequestParam String username, @RequestParam String password) {
        return """
                <!doctype html><html><body style="font: 14px system-ui, sans-serif; margin: 12px; color: #1a7f37">
                ✅ The browser posted <b>username=%s</b> and a password of %d characters to /native-login.
                The values never went through the Vaadin session.
                </body></html>
                """.formatted(HtmlUtils.htmlEscape(username), password.length());
    }
}
