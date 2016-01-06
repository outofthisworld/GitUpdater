package git.sync.listener;

/**
 * Created by Unknown on 6/01/2016.
 */
public interface IGitParseListener extends ParseListener {
    public abstract String getLatestRevision();
}
