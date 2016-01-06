package git.sync.updaters;

import git.sync.http.IHttpDownloader;

/**
 * Created by Unknown on 6/01/2016.
 */
public abstract class HttpUpdater extends Updater {
    private final IHttpDownloader iHttpDownloader;

    public HttpUpdater(IHttpDownloader httpDownloader){
        this.iHttpDownloader = httpDownloader;
    }

    public IHttpDownloader getHttpDownloader(){
        return iHttpDownloader;
    }
}
