package git.sync.updaters;

import git.sync.exception.ProjectRevisionException;
import git.sync.git.GitUpdateDetails;
import git.sync.http.IHttpDownloader;
import git.sync.listener.IGitParseListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class GitUpdater extends HttpUpdater {
    private final String GIT_API_URL;
    private IGitParseListener parseListener;
    private GitUpdateDetails updateDetails;
    private String masterDownloadUrl;
    private Path downloadPath;

    public <T extends GitUpdateDetails,U extends IGitParseListener,V extends IHttpDownloader> GitUpdater(T gitUpdateDetails,U parseListener,V httpDownloader){
        super(httpDownloader);
        this.updateDetails = gitUpdateDetails;
        this.parseListener = parseListener;
        GIT_API_URL = String.format("https://api.github.com/repos/%s/%s/commits?", updateDetails.getGitUser(), updateDetails.getRepo());
        masterDownloadUrl = String.format("https://github.com/%s/%s/archive/master.zip", updateDetails.getGitUser(), updateDetails.getRepo());
        downloadPath = getDownloadPath();
    }

    public final <T extends GitUpdateDetails> void setUpdateDetails(T updateDetails) throws IOException {
        this.updateDetails = updateDetails;
    }

    public void setFileDownloadPath(Path path) {
        this.downloadPath = path;
    }

    public void setMasterFileDownloadURL(String url) {
        this.masterDownloadUrl = url;
    }
    public <T extends IGitParseListener> void setParseListener(T listener){
        this.parseListener = listener;
    }

    @Override
    public String getLatestProjectRevision() throws ProjectRevisionException {
        try {
            String content = new String(getHttpDownloader().downloadHttpContent(new URL(GIT_API_URL + updateDetails.createGetQueryString())));
            parseListener.parseResponse(content);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return parseListener.getLatestRevision();
    }

    public URL getDownloadURL() throws MalformedURLException {
        return new URL(masterDownloadUrl);
    }

    public void setDownloadURL(String downloadURL) {
        this.masterDownloadUrl = downloadURL;
    }

    @Override
    public String downloadLatestRevision() throws IOException {
        getHttpDownloader().downloadHttpContent(new URL(masterDownloadUrl), downloadPath);
        return getDownloadPath().toAbsolutePath().toString();
    }

}
