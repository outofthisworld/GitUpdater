package ui;

import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import git.sync.logging.GitLogger;
import git.sync.logging.TextAreaLoggingHandler;
import git.sync.updaters.ConfigGitUpdater;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Unknown on 16/01/2016.
 */
public class GUI extends Application implements Initializable, Runnable {
    private static final String APP_TITLE = "OSBOT";
    private static Logger logger;
    private static Stage primary_stage;

    @FXML
    private Button launchButton;

    @FXML
    private TextArea logArea;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Text percentage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primary_stage = primaryStage;
        primary_stage.setTitle(APP_TITLE);
        primaryStage.setResizable(false);
        try {
            primary_stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Application.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primary_stage.show();
    }

    private final void update() {
        try {
            final ConfigGitUpdater configGitUpdater = new ConfigGitUpdater() {
                @Override
                protected void handleDownload(Path path) {

                }
            };
            configGitUpdater.getHttpDownloader().addDownloadListener((url, bytesRead, totalBytes) -> Platform.runLater(() -> {
                        if (url.equals(configGitUpdater.getFormattedMasterDownloadUrl())) {
                            double progress = (double) bytesRead / (double) totalBytes;
                            progressBar.setProgress(progress);
                            percentage.setText(DecimalFormat.getInstance().format(progress * 100d) + "%");
                        }
                    }
            ));
            try {
                if (!configGitUpdater.isUpToDate()) {
                    configGitUpdater.tryUpdate();
                } else {
                    progressBar.setProgress(100d);
                    logger.log(Level.INFO, "Project is up to date.");
                }
                launchButton.setDisable(false);
            } catch (ProjectRevisionException | IOException | FileVerificationException | URISyntaxException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, e.getStackTrace().toString());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = GitLogger.getGitLogger(this.getClass(), TextAreaLoggingHandler.createTextAreaLoggingHandler(logArea, true));
        Thread updateThread = new Thread(this);
        updateThread.setName("Update-thread");
        updateThread.setPriority(10);
        updateThread.start();
    }

    @Override
    public void run() {
        update();
    }
}