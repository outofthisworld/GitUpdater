package git.sync.http;

import java.net.URL;
import java.nio.file.Path;

/**
 * Created by Unknown on 6/01/2016.
 */
public interface IHttpDownloader {
    public byte[] downloadHttpContent(URL url);

    public void downloadHttpContent(URL url, Path filePath);
}
