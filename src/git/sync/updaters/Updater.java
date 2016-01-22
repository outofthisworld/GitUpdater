package git.sync.updaters;

import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import git.sync.logging.GitLogger;
import git.sync.logging.TextAreaLoggingHandler;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Unknown on 5/01/2016.
 *
 * An abstract implementation of an updater
 */
public abstract class Updater {

    private static Logger logger = GitLogger.getGitLogger(Updater.class);

    static {
        TextAreaLoggingHandler.getCachedHandler().ifPresent(e -> {
            logger.addHandler(e);
        });
    }

    /**
     * Tries to update a local resource based on checking the current and latest revision. If the two revisions do not match, and thus the project is not up to date
     * this method will attempt to download the resource by calling @method downloadLatestRevision. The resource will then be verified in accordance with the @method
     * verifyDownload. A FileVerificationException is thrown if the downloaded file can not be verified via the specified criteria or an IO exception occurs. Once the updated resource has
     * been downloaded locally, the project revision will be changed and the local file can be handled via the @method handleDownload(Path path).
     *
     * @throws IllegalStateException     If the abstract implementation of the method getDownloadFileName() returns null.
     * @throws IOException               If an IO exception occurs whilst trying to download the updated resouce.
     * @throws ProjectRevisionException  If the abstract implementations of @method getCurrentProjectRevision or @method getLatestProjectRevision return null.
     * @throws FileVerificationException If an error occurs whilst verifying the updated, new revision of the resource.
     * @throws URISyntaxException        If the abstract @method changeProjectRevision throws a URISyntaxException.
     */
    public void tryUpdate() throws IllegalStateException, IOException, ProjectRevisionException, FileVerificationException, URISyntaxException {

        final String currentProjectRevision = getCurrentProjectRevision();
        logger.log(Level.INFO, String.format("Obtained current project revision: HASH: %s", currentProjectRevision));
        final String latestProjectRevision = getLatestProjectRevision();
        logger.log(Level.INFO, String.format("Obtained latest project revision: HASH: %s ", latestProjectRevision));

        if (getDownloadFileName() == null) {
            throw new InvalidStateException("getDownloadFileName() in updater is returning null");
        }

        if (currentProjectRevision == null || currentProjectRevision.equals("") || latestProjectRevision == null)
            throw new ProjectRevisionException("Error: either current project revision or latest project revision return null, cannot verify if the project is up to date");

        if (isUpToDate(currentProjectRevision, latestProjectRevision)) {
            logger.log(Level.INFO, "Project is up to date.");
            return;
        }

        logger.log(Level.INFO, "Downloading latest project revision");
        Path filePath = Paths.get(downloadLatestRevision());

        if (!pathExists(filePath))
            throw new FileNotFoundException("Could not find the latest download revision file");

        try {
            logger.log(Level.INFO, "Verifying downloaded file...");
            if (verifyDownload(getDownloadURL(), filePath, latestProjectRevision)) {
                logger.log(Level.INFO, "Successfully verified downloaded file....");
                changeProjectRevision(currentProjectRevision, latestProjectRevision);
                logger.log(Level.INFO, "Updated current project revision..");
                handleDownload(filePath);
                logger.log(Level.INFO, "Handling file download...");
            } else {
                throw new FileVerificationException("Could not verify downloaded file");
            }
        } catch (IOException | FileVerificationException e) {
            if (pathExists(filePath))
                Files.delete(filePath);
            throw new FileVerificationException("Could not verify the downloaded file.. if the file existed it has now been deleted.", e);
        }
        logger.log(Level.INFO, "Successfully updated project");
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
