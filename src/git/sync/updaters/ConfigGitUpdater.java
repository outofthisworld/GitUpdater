package git.sync.updaters;

import git.sync.git.GitUpdateDetails;
import git.sync.http.HttpDownloader;
import git.sync.listener.GitParseListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipFile;

/**
 * Created by Unknown on 6/01/2016.
 */
public abstract class ConfigGitUpdater extends GitUpdater {
    public static final Properties GIT_CONFIG = new Properties();
    public static final String GIT_CONFIG_LOCATION = "/UpdateConfig.conf";
    private static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    static {
        try {
            GIT_CONFIG.load(ConfigGitUpdater.class.getResourceAsStream(GIT_CONFIG_LOCATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigGitUpdater() throws MalformedURLException {
        super(new GitUpdateDetails(GIT_CONFIG.getProperty("Git.Repository"),
                        GIT_CONFIG.getProperty("Git.User")),
                new GitParseListener(),
                HttpDownloader.getInstance());
    }

    @Override
    public String getCurrentProjectRevision() {
        String currentRevision = GIT_CONFIG.getProperty("Git." + getGitUpdateDetails().getGitUser() + "." + getGitUpdateDetails().getRepo() + ".Revision");
        return currentRevision == null ? "" : currentRevision;
    }

    @Override
    public Path getDownloadPath() {
        return Paths.get(System.getProperty("user.home") +
                File.separator +
                getDownloadFileName());
    }

    public String getDownloadFileName() {
        return GIT_CONFIG.getProperty("Git." + getGitUpdateDetails().getGitUser() + "." + getGitUpdateDetails().getRepo() + ".FileName");
    }

    @Override
    public boolean changeProjectRevision(String oldRevision, String newRevision) throws IOException, URISyntaxException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
        String iso_8601_date = simpleDateFormat.format(new Date());
        GIT_CONFIG.setProperty("Git." + getGitUpdateDetails().getGitUser() + "." + getGitUpdateDetails().getRepo() + ".Revision", newRevision);
        GIT_CONFIG.setProperty("Git." + getGitUpdateDetails().getGitUser() + "." + getGitUpdateDetails().getRepo() + ".LastRevisionChange", iso_8601_date);
        GIT_CONFIG.store(new FileOutputStream(new File(Thread.currentThread().getContextClassLoader()
                .getResource(GIT_CONFIG_LOCATION.substring(1, GIT_CONFIG_LOCATION.length())).toURI().getPath())), "Last revision update on: " + iso_8601_date);
        return true;
    }

    @Override
    public boolean verifyDownload(URL sourceDownloadURL, Path downloadedFilePath, String latestProjectRevision) throws IOException {
        ZipFile zipFile = new ZipFile(downloadedFilePath.toFile());
        return getHttpDownloader().getContentLength(sourceDownloadURL) == downloadedFilePath.toFile().length() &&
                zipFile.getComment().equals(latestProjectRevision);
    }
}
