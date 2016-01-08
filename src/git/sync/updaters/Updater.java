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
public abstract class Updater {

    /**
     * Try update.
     *
     * @throws IllegalStateException     the illegal state exception
     * @throws IOException               the io exception
     * @throws ProjectRevisionException  the project revision exception
     * @throws FileVerificationException the file verification exception
     * @throws URISyntaxException        the uri syntax exception
     */
    public void tryUpdate() throws IllegalStateException, IOException, ProjectRevisionException, FileVerificationException, URISyntaxException {
        final String currentProjectRevision = getCurrentProjectRevision();
        final String latestProjectRevision = getLatestProjectRevision();

        if (getDownloadFileName() == null)
            throw new InvalidStateException("getDownloadFileName() in updater is returning null");

        if (currentProjectRevision == null || latestProjectRevision == null)
            throw new ProjectRevisionException("Either current project revision or latest project revision return null");

        if (isUpToDate(currentProjectRevision, latestProjectRevision))
            return;

        Path filePath = Paths.get(downloadLatestRevision());

        if (!pathExists(filePath))
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

    /**
     * Path exists boolean.
     *
     * @param filePath the file path
     * @return the boolean
     */
    protected boolean pathExists(Path filePath) {
        return filePath != null && Files.exists(filePath);
    }

    /**
     * Getter for property 'upToDate'.
     *
     * @return Value for property 'upToDate'.
     * @throws ProjectRevisionException the project revision exception
     */
    public boolean isUpToDate() throws ProjectRevisionException {
        return getCurrentProjectRevision().equals(getLatestProjectRevision()) && pathExists(getDownloadPath());
    }

    private boolean isUpToDate(String currentRevision, String latestRevision) {
        return currentRevision.equals(latestRevision) && pathExists(getDownloadPath());
    }

    /**
     * Getter for property 'currentProjectRevision'.
     *
     * @return Value for property 'currentProjectRevision'.
     */
    public abstract String getCurrentProjectRevision();

    /**
     * Getter for property 'latestProjectRevision'.
     *
     * @return Value for property 'latestProjectRevision'.
     */
    public abstract String getLatestProjectRevision();

    /**
     * Download latest revision string.
     *
     * @return the string
     * @throws IOException the io exception
     */
    public abstract String downloadLatestRevision() throws IOException;

    /**
     * Getter for property 'downloadPath'.
     *
     * @return Value for property 'downloadPath'.
     */
    public abstract Path getDownloadPath();

    /**
     * Getter for property 'downloadFileName'.
     *
     * @return Value for property 'downloadFileName'.
     */
    public abstract String getDownloadFileName();

    /**
     * Getter for property 'downloadURL'.
     *
     * @return Value for property 'downloadURL'.
     */
    public abstract URL getDownloadURL();

    /**
     * Change project revision boolean.
     *
     * @param oldRevision the old revision
     * @param newRevision the new revision
     * @return the boolean
     * @throws ProjectRevisionException the project revision exception
     * @throws IOException              the io exception
     * @throws URISyntaxException       the uri syntax exception
     */
    protected abstract boolean changeProjectRevision(String oldRevision, String newRevision) throws ProjectRevisionException, IOException, URISyntaxException;

    /**
     * Verify download boolean.
     *
     * @param sourceDownloadURL     the source download url
     * @param downloadedFilePath    the downloaded file path
     * @param latestProjectRevision the latest project revision
     * @return the boolean
     * @throws IOException the io exception
     */
    protected abstract boolean verifyDownload(URL sourceDownloadURL, Path downloadedFilePath, String latestProjectRevision) throws IOException;

    /**
     * Handle download.
     *
     * @param path the path
     */
    protected abstract void handleDownload(Path path);
}
