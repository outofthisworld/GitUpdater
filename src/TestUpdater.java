import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import git.sync.updaters.ConfigGitUpdater;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * Created by Unknown on 5/01/2016.
 */
public class TestUpdater {
    public static void main(String[] args) {
        ConfigGitUpdater gitUpdater = new ConfigGitUpdater() {
            @Override
            public void handleDownload(Path path) {
                System.out.println(path.toString());
            }
        };
        try {
            if (ConfigGitUpdater.gitConfig.contains("Git.LastRevisionChange"))
                gitUpdater.getGitUpdateDetails().addParam("since", ConfigGitUpdater.gitConfig.get("Git.LastRevisionChange"));
            if (!gitUpdater.isUpToDate()) {
                gitUpdater.tryUpdate();
            } else {
                System.out.println("Project is up to date");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProjectRevisionException e) {
            e.printStackTrace();
        } catch (FileVerificationException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
