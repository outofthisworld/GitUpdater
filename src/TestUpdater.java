import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import git.sync.updaters.ConfigGitUpdater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * Created by Unknown on 5/01/2016.
 */
public class TestUpdater {
    public static void main(String[] args) {
        ConfigGitUpdater gitUpdater = null;
        try {
            gitUpdater = new ConfigGitUpdater() {
                @Override
                public void handleDownload(Path path) {
                    System.out.println(path.toString());
                }
            };
            gitUpdater.getHttpDownloader().addDownloadListener((i, v, u) -> {
                System.out.println("Downloading from url " + i);
                System.out.println("Percentage = " + (double) v / u);
            });
            try {
                if (!gitUpdater.isUpToDate()) {
                    gitUpdater.tryUpdate();
                } else {
                    System.out.println("Project is up to date");
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ProjectRevisionException e) {
                e.printStackTrace();
            } catch (FileVerificationException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
