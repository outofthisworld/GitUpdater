package git.sync.updaters;

import git.sync.http.IHttpDownloader;

/**
 * Created by Unknown on 6/01/2016.
 */
public abstract class HttpUpdater<T extends IHttpDownloader> extends Updater {
    private final T iHttpDownloader;

    public HttpUpdater(T httpDownloader) {
        this.iHttpDownloader = httpDownloader;
    }

    public T getHttpDownloader() {
        return iHttpDownloader;
    }
}
