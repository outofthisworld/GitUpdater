package git.sync.updaters;

import git.sync.exception.ProjectRevisionException;

import javax.xml.ws.http.HTTPException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class Updater{
    public abstract String getCurrentProjectRevision() throws ProjectRevisionException;
    public abstract String getLatestProjectRevision() throws ProjectRevisionException;
    public abstract String downloadLatestRevision() throws HTTPException;
    public abstract boolean changeProjectRevision(String oldRevision, String newRevision) throws ProjectRevisionException;
    public abstract void handleDownload(Path path);


    public boolean pathExists(Path filePath){return filePath != null && Files.exists(filePath);}

    public boolean projectNeedsUpdating() throws ProjectRevisionException {return !getCurrentProjectRevision().equals(getLatestProjectRevision());}

    public boolean tryUpdate() throws FileNotFoundException, ProjectRevisionException {
        final String currentProjectRevision = getCurrentProjectRevision();
        final String latestProjectRevision = getLatestProjectRevision();

        if(currentProjectRevision == null || latestProjectRevision == null)
            throw new ProjectRevisionException("Either current project revision or latest project revision return null");

        if(currentProjectRevision.equals(latestProjectRevision))
            return false;

        Path filePath = Paths.get(downloadLatestRevision());

        if(!pathExists(filePath))
            throw new FileNotFoundException("Could not find the latest download revision file");

        changeProjectRevision(currentProjectRevision,latestProjectRevision);
        handleDownload(filePath);
        return true;
    }
}
