package git.sync.updaters;

import git.sync.git.GitUpdateDetails;
import git.sync.http.HttpDownloader;
import git.sync.listener.IGitParseListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class GitUpdater extends HttpUpdater<HttpDownloader> {
    private static final String GIT_API_URL = "https://api.github.com/repos/%s/%s/commits?";
    private static final String MASTER_DOWNLOAD_URL = "https://github.com/%s/%s/archive/";
    private URL formattedGitApiUrl;
    private URL formattedMasterDownloadUrl;
    private IGitParseListener parseListener;
    private GitUpdateDetails updateDetails;
    private Path downloadPath;

    public <T extends GitUpdateDetails, U extends IGitParseListener, V extends HttpDownloader> GitUpdater(T gitUpdateDetails, U parseListener, V httpDownloader) throws MalformedURLException {
        super(httpDownloader);
        this.updateDetails = gitUpdateDetails;
        this.parseListener = parseListener;
        formattedGitApiUrl = new URL(String.format(GIT_API_URL, updateDetails.getGitUser(), updateDetails.getRepo()));
        formattedMasterDownloadUrl = new URL(String.format(MASTER_DOWNLOAD_URL, updateDetails.getGitUser(), updateDetails.getRepo()) + getDownloadFileName());
        downloadPath = getDownloadPath();
    }

    public final <T extends GitUpdateDetails> void setUpdateDetails(T updateDetails) throws IOException {
        this.updateDetails = updateDetails;
        refreshGitApiURL();
        refreshMasterDownloadURL();
    }

    public void setRepo(String repo) throws MalformedURLException {
        GitUpdateDetails gitUpdateDetails = new GitUpdateDetails(repo, updateDetails.getGitUser());
        transferParams(gitUpdateDetails);
        this.updateDetails = gitUpdateDetails;
        refreshGitApiURL();
        refreshMasterDownloadURL();
    }

    public void setGitUser(String gitUser) throws MalformedURLException {
        GitUpdateDetails gitUpdateDetails = new GitUpdateDetails(updateDetails.getRepo(), gitUser);
        transferParams(gitUpdateDetails);
        this.updateDetails = gitUpdateDetails;
        refreshGitApiURL();
        refreshMasterDownloadURL();
    }

    public void setGitUserRepo(String gitUser, String repo) throws MalformedURLException {
        GitUpdateDetails gitUpdateDetails = new GitUpdateDetails(gitUser, repo);
        transferParams(gitUpdateDetails);
        this.updateDetails = gitUpdateDetails;
        refreshGitApiURL();
        refreshMasterDownloadURL();
    }

    public GitUpdateDetails getGitUpdateDetails() {
        return updateDetails;
    }

    public <T extends GitUpdateDetails> void setGitUpdateDetails(T gitUpdateDetails) throws MalformedURLException {
        this.updateDetails = gitUpdateDetails;
        refreshGitApiURL();
        refreshMasterDownloadURL();
    }

    private final <T extends GitUpdateDetails> void transferParams(T updateDetails) {
        for (Map.Entry<String, String> en : this.updateDetails.getParams().entrySet()) {
            updateDetails.addParam(en.getKey(), en.getValue());
        }
    }

    private final void refreshGitApiURL() throws MalformedURLException {
        formattedGitApiUrl = new URL(String.format(GIT_API_URL, updateDetails.getGitUser(), updateDetails.getRepo()));
    }

    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }

    private final void refreshMasterDownloadURL() throws MalformedURLException {
        formattedMasterDownloadUrl = new URL(String.format(MASTER_DOWNLOAD_URL, updateDetails.getGitUser(), updateDetails.getRepo()));
    }

    public <T extends IGitParseListener> void setParseListener(T listener) {
        this.parseListener = listener;
    }

    @Override
    public String getLatestProjectRevision() {
        try {
            URL downloadURL = new URL(formattedGitApiUrl + updateDetails.createGetQueryString());
            String content = new String(getHttpDownloader().downloadHttpContent(downloadURL));
            parseListener.parseResponse(content);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return parseListener.getLatestRevision();
    }

    @Override
    public URL getDownloadURL() {
        return formattedMasterDownloadUrl;
    }

    @Override
    public String downloadLatestRevision() throws IOException {
        getHttpDownloader().downloadHttpContent(formattedMasterDownloadUrl, downloadPath);
        return downloadPath.toAbsolutePath().toString();
    }

}
