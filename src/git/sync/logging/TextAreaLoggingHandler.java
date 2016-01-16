package git.sync.logging;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Unknown on 16/01/2016.
 */
public class TextAreaLoggingHandler extends Handler {
    private static TextAreaLoggingHandler textAreaLoggingHandler = null;
    private final TextArea textArea;

    private TextAreaLoggingHandler(TextArea textArea) {
        this.textArea = textArea;
    }

    public static Optional<TextAreaLoggingHandler> getCachedHandler() {
        return Optional.of(textAreaLoggingHandler);
    }

    public static final TextAreaLoggingHandler createTextAreaLoggingHandler(TextArea textArea, boolean cache) {
        Objects.requireNonNull(textArea);
        if (cache) {
            textAreaLoggingHandler = new TextAreaLoggingHandler(textArea);
            return textAreaLoggingHandler;
        }
        return new TextAreaLoggingHandler(textArea);
    }

    @Override
    public void publish(LogRecord record) {
        final StringBuilder stringBuilder = new StringBuilder().append(textArea.getText()).append(System.getProperty("line.separator"));

        stringBuilder.append("Class: ").append(record.getLoggerName()).append(", Sequence: ").append(record.getSequenceNumber()).append(", Thread: ").append(record.getThreadID())
                .append(": ").append(record.getMessage()).append(System.getProperty("line.separator"));
        Platform.runLater(() -> textArea.setText(stringBuilder.toString()));

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
