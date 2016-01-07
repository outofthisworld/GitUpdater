package git.sync.updaters;

import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class Updater{

    public void tryUpdate() throws IllegalStateException, IOException, ProjectRevisionException, FileVerificationException, URISyntaxException {
        final String currentProjectRevision = getCurrentProjectRevision();
        final String latestProjectRevision = getLatestProjectRevision();

        if (getDownloadFileName() == null)
            throw new InvalidStateException("getDownloadFileName() in updater is returning null");

        if(currentProjectRevision == null || latestProjectRevision == null)
            throw new ProjectRevisionException("Either current project revision or latest project revision return null");

        if (isUpToDate(currentProjectRevision, latestProjectRevision))
            return;

        Path filePath = Paths.get(downloadLatestRevision());

        if(!pathExists(filePath))
            throw new FileNotFoundException("Could not find the latest download revision file");

        try {
            if (verifyDownload(getDownloadURL(), filePath, latestProjectRevision)) {
                changeProjectRevision(currentProjectRevision, latestProjectRevision);
                handleDownload(filePath);
            }
        } catch (IOException e) {
            if (pathExists(filePath))
                Files.delete(filePath);
            throw new FileVerificationException("Could not verify the downloaded file.. if the file existed it has now been deleted.");
        }
    }

    protected boolean pathExists(Path filePath) {
        return filePath != null && Files.exists(filePath);
    }

    public boolean isUpToDate() throws ProjectRevisionException {
        return getCurrentProjectRevision().equals(getLatestProjectRevision());
    }

    private boolean isUpToDate(String currentRevision, String latestRevision) {
        return currentRevision.equals(latestRevision);
    }

    public abstract String getCurrentProjectRevision();

    public abstract String getLatestProjectRevision();

    public abstract String downloadLatestRevision() throws IOException;

    public abstract Path getDownloadPath();

    public abstract String getDownloadFileName();

    public abstract URL getDownloadURL();

    protected abstract boolean changeProjectRevision(String oldRevision, String newRevision) throws ProjectRevisionException, IOException, URISyntaxException;

    protected abstract boolean verifyDownload(URL sourceDownloadURL, Path downloadedFilePath, String latestProjectRevision) throws IOException;

    protected abstract void handleDownload(Path path);
}
