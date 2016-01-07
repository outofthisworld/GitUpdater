package git.sync.updaters;

import git.sync.exception.ProjectRevisionException;
import git.sync.git.GitUpdateDetails;
import git.sync.http.HttpDownloader;
import git.sync.listener.GitParseListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Unknown on 6/01/2016.
 */
public abstract class ConfigGitUpdater extends GitUpdater {
    public static final Properties gitConfig = new Properties();
    private static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    static {
        try {
            gitConfig.load(ConfigGitUpdater.class.getResourceAsStream("/UpdateConfig.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigGitUpdater() {
        super(new GitUpdateDetails(gitConfig.getProperty("Git.Repository"),
                        gitConfig.getProperty("Git.User"),
                        gitConfig.getProperty("Git.BranchOrSha")),
                new GitParseListener(),
                new HttpDownloader());
    }

    @Override
    public String getCurrentProjectRevision() throws ProjectRevisionException {
        return gitConfig.getProperty("Git.CurrentProjectRevision");
    }

    @Override
    public Path getDownloadPath() {
        return Paths.get(System.getProperty("user.home") +
                File.separator +
                gitConfig.getProperty("Git.Download.FileName"));
    }

    @Override
    public boolean changeProjectRevision(String oldRevision, String newRevision) throws IOException, URISyntaxException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
        String iso_8601_date = simpleDateFormat.format(new Date());
        gitConfig.setProperty("Git.CurrentProjectRevision", newRevision);
        gitConfig.setProperty("Git.LastRevisionChange", iso_8601_date);
        gitConfig.store(new FileOutputStream(new File(Thread.currentThread().getContextClassLoader()
                .getResource("UpdateConfig.conf").toURI().getPath())), "Last revision update on: " + iso_8601_date);
        return true;
    }

    @Override
    public boolean verifyDownload(URL sourceDownloadURL, Path downloadedFilePath, String latestProjectRevision) throws ProjectRevisionException {
        return true;
    }
}
