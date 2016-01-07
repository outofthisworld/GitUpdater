package git.sync.listener;

import java.net.URL;

/**
 * Created by Unknown on 7/01/2016.
 */
public interface DownloadListener {
    public void contentDownloaded(URL url, int bytesRead, int totalBytes);
}
