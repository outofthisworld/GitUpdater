package git.sync.updaters;

import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;

import javax.xml.ws.http.HTTPException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class Updater{
    public abstract String getCurrentProjectRevision() throws ProjectRevisionException;
    public abstract String getLatestProjectRevision() throws ProjectRevisionException;

    public abstract String downloadLatestRevision() throws HTTPException, IOException;

    public abstract Path getDownloadPath();

    public abstract URL getDownloadURL() throws MalformedURLException;
    public abstract boolean changeProjectRevision(String oldRevision, String newRevision) throws ProjectRevisionException;

    public abstract boolean verifyDownload(URL sourceDownloadURL, Path downloadedFilePath);
    public abstract void handleDownload(Path path);


    public boolean pathExists(Path filePath){return filePath != null && Files.exists(filePath);}
    public boolean projectNeedsUpdating() throws ProjectRevisionException {return !getCurrentProjectRevision().equals(getLatestProjectRevision());}

    private boolean isUpToDate(String currentRevision, String latestRevision) {
        return !currentRevision.equals(latestRevision);
    }

    public boolean tryUpdate() throws IOException, ProjectRevisionException, FileVerificationException {
        final String currentProjectRevision = getCurrentProjectRevision();
        final String latestProjectRevision = getLatestProjectRevision();

        if(currentProjectRevision == null || latestProjectRevision == null)
            throw new ProjectRevisionException("Either current project revision or latest project revision return null");

        if (!isUpToDate(currentProjectRevision, latestProjectRevision))
            return false;

        Path filePath = Paths.get(downloadLatestRevision());

        if(!pathExists(filePath))
            throw new FileNotFoundException("Could not find the latest download revision file");

        if (!verifyDownload(getDownloadURL(), filePath))
            throw new FileVerificationException("Could not verify the downloaded file");

        changeProjectRevision(currentProjectRevision,latestProjectRevision);
        handleDownload(filePath);
        return true;
    }
}
