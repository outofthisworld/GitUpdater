package Json.Git;

import Updater.Git.ParseListener;

/**
 * Created by Unknown on 6/01/2016.
 */
public interface GitParseListener extends ParseListener {
    public abstract String getLatestRevision();
}
