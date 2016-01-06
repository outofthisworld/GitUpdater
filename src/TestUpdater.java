import git.sync.listener.GitParseListener;
import git.sync.exception.ProjectRevisionException;
import git.sync.git.GitUpdateDetails;
import git.sync.git.GitUpdater;
import git.sync.http.HttpDownloader;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Unknown on 5/01/2016.
 */
public class TestUpdater {
    public static void main(String[] args) {
        try {
            GitUpdater gitUpdater = new GitUpdater(
                    new GitUpdateDetails("HttpServer","outofthisworld","master"), new GitParseListener(),new HttpDownloader()) {
                @Override
                public String getCurrentProjectRevision() throws ProjectRevisionException {
                    return "122";
                }

                @Override
                public boolean changeProjectRevision(String oldRevision, String newRevision) throws ProjectRevisionException {
                    System.out.println("Old revision: " + oldRevision);
                    System.out.println("New revision: " + newRevision);
                    return false;
                }

                @Override
                public void handleDownload(Path path) {
                    System.out.println("Downloaded file to: " + path.toAbsolutePath());
                }
            };
            System.out.println(gitUpdater.projectNeedsUpdating());
            gitUpdater.tryUpdate();
            gitUpdater.tryUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProjectRevisionException e) {
            e.printStackTrace();
        }
    }
}
