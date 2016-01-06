package git.sync.http;

import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Unknown on 6/01/2016.
 */
public interface IHttpDownloader {
    public String downloadHttpContent(URL url,Charset defaultCharset);
}
