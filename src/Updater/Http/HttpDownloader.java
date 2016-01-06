package Updater.Http;

import Updater.Exception.ProjectRevisionException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Unknown on 6/01/2016.
 */
public class HttpDownloader implements IHttpDownloader {
    public Charset getHttpContentEncoding(HttpURLConnection con,Charset defaultCharset){
        String encoding;
        if((encoding = con.getContentEncoding()) == null)
            return defaultCharset;

        return Charset.forName(encoding);
    }

    public HttpURLConnection establishHttpUrlConnection(URL url) throws IOException {
        HttpURLConnection gitConnectURL = ((HttpURLConnection) url.openConnection());
        gitConnectURL.connect();
        return gitConnectURL;
    }

    public ReadableByteChannel createReadableByteChannel(HttpURLConnection in) throws IOException {
        return Channels.newChannel(in.getInputStream());
    }

    public ByteBuffer readChannel(ReadableByteChannel channel,int size) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        channel.read(byteBuffer);
        channel.close();
        return byteBuffer;
    }

    @Override
    public String downloadHttpContent(URL url,Charset defaultCharset) {
        String content = null;
        try {
            HttpURLConnection gitConnectURL = establishHttpUrlConnection(
                    url);
            content= new String(
                    readChannel(createReadableByteChannel(gitConnectURL), gitConnectURL.getContentLength()).array(),
                    getHttpContentEncoding(gitConnectURL, defaultCharset
            ));
        } catch (IOException e) {
            return content;
        }
        return content;
    }
}
