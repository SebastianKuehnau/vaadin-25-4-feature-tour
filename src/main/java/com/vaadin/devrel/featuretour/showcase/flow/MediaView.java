package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Audio;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Media;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.NativeForm;
import com.vaadin.flow.component.html.Source;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Video;
import com.vaadin.flow.component.html.AnchorTargetValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.js.JsDefinition;
import com.vaadin.flow.js.JsExpression;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Route("media-and-native-form")
@PageTitle("Media & Native Form")
@Menu(title = "Media & Native Form", parent = FlowArea.class, order = 1)
@ShowcaseInfo(summary = "Video, Audio and Source from a DownloadHandler with seeking, and NativeForm.", tiers = Tier.FREE)
public class MediaView extends ShowcasePage {

    @JsDefinition
    public interface MediaJs extends Serializable {
        @JsExpression("this.currentTime = $0; this.play()")
        void seek(double seconds);
    }

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    final Video video = new Video();
    final Audio audio = new Audio();
    final Div rangeLog = new Div();
    final NativeForm form = new NativeForm();

    public MediaView() {
        super("Media & Native Form",
                Messages.get("flow.media.lead"),
                "https://vaadin.com/docs/next/components/html-elements");

        addVideoDemo();
        addAudioDemo();
        addFormDemo();
    }

    private void addVideoDemo() {
        rangeLog.addClassName("csp-warnings");
        rangeLog.setTestId("range-log");
        rangeLog.add(new Span(Messages.get("flow.media.rangeLog.empty")));

        video.addSource(new RangeLoggingHandler(media("feature-tour.webm"), this::logRange), "video/webm");
        video.addSource(new RangeLoggingHandler(media("feature-tour.mp4"), this::logRange), "video/mp4");
        video.setPoster(DownloadHandler.forClassResource(MediaView.class, "/media/poster.jpg"));
        video.setControlsVisible(true);
        video.setPreload(Media.Preload.METADATA);
        video.setWidth("480px");
        video.setTestId("video");

        var jump = new Button(Messages.get("flow.media.seek"), e -> video.getElement().executeJs(MediaJs.class).seek(8));
        jump.setTestId("seek");

        addDemo(Messages.get("flow.media.video.heading"),
                Messages.get("flow.media.video.desc"),
                new VerticalLayout(video, new FlexLayout(jump), rangeLog), """
                        Video video = new Video();
                        video.addSource(DownloadHandler.forClassResource(MediaView.class, "/media/feature-tour.webm")
                                .enableRangeRequests(), "video/webm");
                        video.addSource(DownloadHandler.forClassResource(MediaView.class, "/media/feature-tour.mp4")
                                .enableRangeRequests(), "video/mp4");
                        video.setPoster(DownloadHandler.forClassResource(MediaView.class, "/media/poster.jpg"));
                        video.setControlsVisible(true);
                        video.setPreload(Media.Preload.METADATA);

                        // DownloadHandler.forFile(...) always answers range requests
                        """);
    }

    private static DownloadHandler media(String name) {
        // Resources from a jar only answer ranges when asked to
        return DownloadHandler.forClassResource(MediaView.class, "/media/" + name).enableRangeRequests();
    }

    private void logRange(String range) {
        // Download requests run outside the session lock
        getUI().ifPresent(ui -> ui.access(() -> {
            if (rangeLog.getChildren().findFirst().filter(Span.class::isInstance).isPresent()) {
                rangeLog.removeAll();
            }
            var line = new Div(new Span(LocalTime.now().format(TIME)),
                    new Span(range == null ? Messages.get("flow.media.rangeLog.whole") : range));
            line.addClassName("csp-warning");
            rangeLog.addComponentAsFirst(line);
            while (rangeLog.getComponentCount() > 6) {
                rangeLog.remove(rangeLog.getComponentAt(rangeLog.getComponentCount() - 1));
            }
        }));
    }

    private void addAudioDemo() {
        audio.add(new Source(media("sweep.ogg"), "audio/ogg"), new Source(media("sweep.mp3"), "audio/mpeg"));
        audio.setControlsVisible(true);
        audio.setTestId("audio");
        addDemo(Messages.get("flow.media.audio.heading"),
                Messages.get("flow.media.audio.desc"),
                audio, """
                        Audio audio = new Audio(
                                new Source(handler("sweep.ogg"), "audio/ogg"),
                                new Source(handler("sweep.mp3"), "audio/mpeg"));
                        audio.setControlsVisible(true);
                        """);
    }

    private void addFormDemo() {
        var username = new Input();
        username.setName("username");
        username.setAutocomplete("username");
        username.setId("native-username");
        var password = new Input();
        password.setType("password");
        password.setName("password");
        password.setAutocomplete("current-password");
        password.setId("native-password");
        // Keep the password out of the Vaadin session
        password.setValueChangeMode(null);
        var submit = new NativeButton(Messages.get("flow.media.form.login"));
        submit.setType(NativeButton.Type.SUBMIT);

        form.add(field(Messages.get("flow.media.form.username"), username),
                field(Messages.get("flow.media.form.password"), password), submit);
        form.setAction("/native-login");
        form.setMethod(NativeForm.Method.POST);
        form.setTarget(AnchorTargetValue.forString("login-result"));
        form.addClassName("native-login");
        form.setTestId("native-form");

        var result = new IFrame();
        result.setName("login-result");
        result.addClassName("login-result");

        addDemo(Messages.get("flow.media.form.heading"),
                Messages.get("flow.media.form.desc"),
                new VerticalLayout(form, result), """
                        Input username = new Input();
                        username.setName("username");
                        username.setAutocomplete("username");
                        Input password = new Input();
                        password.setType("password");
                        password.setName("password");
                        password.setAutocomplete("current-password");
                        password.setValueChangeMode(null); // keep it out of the session

                        NativeButton submit = new NativeButton("Log in");
                        submit.setType(NativeButton.Type.SUBMIT);

                        NativeForm form = new NativeForm(username, password, submit);
                        form.setAction("/native-login");
                        form.setMethod(NativeForm.Method.POST);
                        """);
    }

    private static Component field(String label, Input input) {
        var text = new NativeLabel(label);
        text.setFor(input);
        var field = new Div(text, input);
        field.addClassName("native-field");
        return field;
    }
}
