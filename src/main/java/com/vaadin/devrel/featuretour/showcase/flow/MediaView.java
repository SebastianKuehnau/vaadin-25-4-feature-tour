package com.vaadin.devrel.featuretour.showcase.flow;

import com.vaadin.devrel.featuretour.base.ui.LogTable;
import com.vaadin.devrel.featuretour.base.ui.ShowcaseInfo;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;
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
import com.vaadin.flow.component.html.Video;
import com.vaadin.flow.component.html.AnchorTargetValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.js.JsDefinition;
import com.vaadin.flow.js.JsExpression;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.io.Serializable;

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

    final LogTable rangeLog = new LogTable("Requests from the player appear here.", 6);

    public MediaView() {
        super("Media & Native Form",
                "New HTML components for the browser's own media players and forms. Media is served from a "
                        + "DownloadHandler that answers HTTP range requests, so players can seek.",
                "https://vaadin.com/docs/next/components/html-elements");

        addVideoDemo();
        addAudioDemo();
        addFormDemo();
    }

    private static DownloadHandler media(String name) {
        // Resources from a jar only answer ranges when asked to
        return DownloadHandler.forClassResource(MediaView.class, "/media/" + name).enableRangeRequests();
    }

    /** The tour video in WebM and MP4, with a poster. Each source reports the byte ranges the player asks for. */
    static class TourVideo extends Video {
        TourVideo(SerializableConsumer<String> rangeListener) {
            addSource(new RangeLoggingHandler(media("feature-tour.webm"), rangeListener), "video/webm");
            addSource(new RangeLoggingHandler(media("feature-tour.mp4"), rangeListener), "video/mp4");
            setPoster(DownloadHandler.forClassResource(MediaView.class, "/media/poster.jpg"));
            setControlsVisible(true);
            setPreload(Media.Preload.METADATA);
            setWidth("480px");
            setTestId("video");
        }

        void seek(double seconds) {
            getElement().executeJs(MediaJs.class).seek(seconds);
        }
    }

    private void addVideoDemo() {
        rangeLog.setTestId("range-log");
        var video = new TourVideo(this::logRange);
        var jump = new Button("Jump to 0:08", e -> video.seek(8));
        jump.setTestId("seek");

        addDemo("Video with seeking",
                "Two sources, WebM and MP4, both served from the classpath. Drag the timeline or jump ahead: the player "
                        + "asks for a byte range and gets 206 Partial Content instead of the whole file.",
                new VerticalLayout(video, new FlexLayout(jump), rangeLog), """
                        class TourVideo extends **Video** {
                            TourVideo() {
                                **addSource**(media("feature-tour.webm"), "video/webm");
                                **addSource**(media("feature-tour.mp4"), "video/mp4");
                                **setPoster**(DownloadHandler.forClassResource(MediaView.class, "/media/poster.jpg"));
                                **setControlsVisible(true)**;
                                **setPreload(Media.Preload.METADATA)**;
                            }
                        }

                        static DownloadHandler media(String name) {
                            // Resources from a jar only answer ranges when asked to.
                            // DownloadHandler.forFile(...) always answers them.
                            return DownloadHandler.forClassResource(MediaView.class, "/media/" + name)
                                    **.enableRangeRequests()**;
                        }
                        """);
    }

    private void logRange(String range) {
        // Download requests run outside the session lock
        getUI().ifPresent(ui -> ui.access(() -> rangeLog.log(range)));
    }

    /** A rising tone, in Ogg and MP3. */
    static class SweepAudio extends Audio {
        SweepAudio() {
            super(new Source(media("sweep.ogg"), "audio/ogg"), new Source(media("sweep.mp3"), "audio/mpeg"));
            setControlsVisible(true);
            setTestId("audio");
        }
    }

    private void addAudioDemo() {
        addDemo("Audio",
                "The same API for audio: a rising tone, with an Ogg and an MP3 source to pick from.",
                new SweepAudio(), """
                        class SweepAudio extends **Audio** {
                            SweepAudio() {
                                super(new **Source**(media("sweep.ogg"), "audio/ogg"),
                                        new **Source**(media("sweep.mp3"), "audio/mpeg"));
                                setControlsVisible(true);
                            }
                        }
                        """);
    }

    /** A native input with a label, the way password managers expect it. */
    static class LabeledInput extends Div {
        LabeledInput(String label, Input input) {
            var text = new NativeLabel(label);
            text.setFor(input);
            add(text, input);
            addClassName("native-field");
        }
    }

    static class UsernameInput extends Input {
        UsernameInput() {
            setName("username");
            setAutocomplete("username");
            setId("native-username");
        }
    }

    static class PasswordInput extends Input {
        PasswordInput() {
            setType("password");
            setName("password");
            setAutocomplete("current-password");
            setId("native-password");
            // Keep the password out of the Vaadin session
            setValueChangeMode(null);
        }
    }

    static class SubmitButton extends NativeButton {
        SubmitButton(String text) {
            super(text);
            setType(NativeButton.Type.SUBMIT);
        }
    }

    /** A login form the browser submits by itself. The response opens in the frame with the given name. */
    static class NativeLoginForm extends NativeForm {
        NativeLoginForm(String target) {
            add(new LabeledInput("Username", new UsernameInput()),
                    new LabeledInput("Password", new PasswordInput()),
                    new SubmitButton("Log in"));
            setAction("/native-login");
            setMethod(NativeForm.Method.POST);
            setTarget(AnchorTargetValue.forString(target));
            addClassName("native-login");
            setTestId("native-form");
        }
    }

    /** Shows the response to the native form, so this page stays open. */
    static class LoginResultFrame extends IFrame {
        LoginResultFrame(String name) {
            setName(name);
            addClassName("login-result");
        }
    }

    private void addFormDemo() {
        addDemo("A native login form",
                "The browser submits a native <form> by itself, so password managers recognize it and offer to save "
                        + "and fill the credentials. Here the response goes into a frame, so this page stays open. "
                        + "With Spring Security, post to its login processing URL instead.",
                new VerticalLayout(new NativeLoginForm("login-result"), new LoginResultFrame("login-result")), """
                        class NativeLoginForm extends **NativeForm** {
                            NativeLoginForm(String target) {
                                add(new LabeledInput("Username", new UsernameInput()),
                                        new LabeledInput("Password", new PasswordInput()),
                                        new SubmitButton("Log in"));
                                **setAction("/native-login")**;
                                **setMethod(NativeForm.Method.POST)**;
                                setTarget(AnchorTargetValue.forString(target)); // the name of an IFrame
                            }
                        }

                        class UsernameInput extends Input {
                            UsernameInput() {
                                setName("username");
                                **setAutocomplete("username")**;
                            }
                        }

                        class PasswordInput extends Input {
                            PasswordInput() {
                                setType("password");
                                setName("password");
                                **setAutocomplete("current-password")**;
                                setValueChangeMode(null); // keep the password out of the Vaadin session
                            }
                        }

                        class SubmitButton extends NativeButton {
                            SubmitButton(String text) {
                                super(text);
                                **setType(NativeButton.Type.SUBMIT)**;
                            }
                        }
                        """);
    }
}
