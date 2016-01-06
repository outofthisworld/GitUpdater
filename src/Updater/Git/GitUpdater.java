package Updater.Git;

import Json.Git.IGitParseListener;
import Updater.Exception.ProjectRevisionException;
import Updater.Http.IHttpDownloader;
import Updater.Updaters.HttpUpdater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class GitUpdater extends HttpUpdater {
    private final String GIT_API_URL;
    private IGitParseListener parseListener;
    private GitUpdateDetails updateDetails;

    public <T extends GitUpdateDetails,U extends IGitParseListener,V extends IHttpDownloader> GitUpdater(T gitUpdateDetails,U parseListener,V httpDownloader){
        super(httpDownloader);
        this.updateDetails = gitUpdateDetails;
        this.parseListener = parseListener;
        GIT_API_URL = String.format("https://api.github.com/repos/%s/%s/commits?", updateDetails.getGitUser(), updateDetails.getRepo());
    }

    public final <T extends GitUpdateDetails> void setUpdateDetails(T updateDetails) throws IOException {
        this.updateDetails = updateDetails;
    }

    public <T extends IGitParseListener> void setParseListener(T listener){
        this.parseListener = listener;
    }

    @Override
    public String getLatestProjectRevision() throws ProjectRevisionException {
        try {
            String content = getHttpDownloader().downloadHttpContent(new URL(GIT_API_URL + updateDetails.createGetQueryString()), StandardCharsets.UTF_8);
            parseListener.parseResponse(content);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return parseListener.getLatestRevision();
    }

    @Override
    public String downloadLatestRevision() {
        return "C:\\Users\\Unknown\\Desktop\\testFile.jpeg";
    }
}
