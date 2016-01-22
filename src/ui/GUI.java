package ui;

import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import git.sync.logging.GitLogger;
import git.sync.logging.TextAreaLoggingHandler;
import git.sync.updaters.ConfigGitUpdater;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import loader.JarClassTransformer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Unknown on 16/01/2016.
 */
public class GUI extends Application implements Initializable {
    private static final String APP_TITLE = "OSBOT";
    private static final GUI gui = new GUI();
    private static Logger logger;
    private static Stage primary_stage;
    private final Executor executor = Executors.newFixedThreadPool(1);
    @FXML
    private Button launchButton;
    @FXML
    private Button retry;
    @FXML
    private TextArea logArea;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Text percentage;
    private final Runnable runnableUpdate = () -> update();

    public static void main(String[] args) {
        launch(args);
    }

    public static GUI getGuiInstance() {
        return gui;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primary_stage = primaryStage;
        primary_stage.setTitle(APP_TITLE);
        primary_stage.setResizable(false);
        setScene("/Application.fxml");
        primaryStage.show();
    }

    public void setScene(URL url) {
        Platform.runLater(() -> {
        try {
            primary_stage.setScene(new Scene(FXMLLoader.load(url)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        });
    }


    public void setScene(URL url, int width, int height) {
        Platform.runLater(() -> {
            try {
                primary_stage.setScene(new Scene(FXMLLoader.load(url), width, height));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setScene(String fxmlResource) {
        setScene(getClass().getResource("/Application.fxml"));
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
                JarClassTransformer jarClassLoader = new JarClassTransformer(new JarFile(new File(getClass().getResource("/gamepack_4127917.jar").getPath())));
                try {
                    jarClassLoader.loadClass("client");
                    jarClassLoader.forEachClass(e -> System.out.println(e.getName()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (ProjectRevisionException | IOException | FileVerificationException | URISyntaxException e) {
                logger.log(Level.SEVERE, e.getMessage());
                retry.setVisible(true);
            }
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, e.getStackTrace().toString());
        }
    }

    @FXML
    void retryUpdate(ActionEvent event) {
        retry.setVisible(false);
        executor.execute(runnableUpdate);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = GitLogger.getGitLogger(this.getClass(), TextAreaLoggingHandler.createTextAreaLoggingHandler(logArea, true));
        executor.execute(runnableUpdate);
    }

}
