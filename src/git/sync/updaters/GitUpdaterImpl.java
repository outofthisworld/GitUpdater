package git.sync.updaters;

import git.sync.exception.ProjectRevisionException;
import git.sync.git.GitUpdateDetails;
import git.sync.http.HttpDownloader;
import git.sync.listener.GitParseListener;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by Unknown on 6/01/2016.
 */
public class GitUpdaterImpl extends GitUpdater {
    private static Properties gitConfig = new Properties();

    static {
        try {
            gitConfig.load(GitUpdaterImpl.class.getResourceAsStream("UpdateConfig.conf"));
        } catch (IOException e) {
            e.getCause();
        }
    }

    {
        System.out.println("in instance");
    }

    public GitUpdaterImpl() {
        super(new GitUpdateDetails(gitConfig.getProperty("Git.Repository"),
                        gitConfig.getProperty("Git.User"),
                        gitConfig.getProperty("Git.ShaOrBranch")),
                new GitParseListener(),
                new HttpDownloader());
        System.out.println("in super");
        System.out.println("in constructor");
    }

    @Override
    public String getCurrentProjectRevision() throws ProjectRevisionException {
        return gitConfig.getProperty("Git.CurrentProjectRevision");
    }

    @Override
    public Path getDownloadPath() {
        return Paths.get(System.getProperty("user.home"));
    }

    @Override
    public boolean changeProjectRevision(String oldRevision, String newRevision) throws ProjectRevisionException {
        gitConfig.setProperty("Git.CurrentProjectRevision", newRevision);
        return false;
    }

    @Override
    public boolean verifyDownload(URL sourceDownloadURL, Path downloadedFilePath) {
        boolean verification = false;
        try {
            verification = ((HttpDownloader) getHttpDownloader()).getContentLength(sourceDownloadURL) == downloadedFilePath.toFile().length();
            System.out.println(verification);
        } catch (IOException e) {
            return verification;
        }
        return verification;
    }

    @Override
    public void handleDownload(Path path) {
        System.out.println("Downloaded file to: " + path.toAbsolutePath());
    }
}
