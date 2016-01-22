package git.sync.http;

import git.sync.listener.DownloadListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 * Created by Unknown on 6/01/2016.
 */
public class HttpDownloader implements IHttpDownloader {
    private static final HttpDownloader httpDownloader = new HttpDownloader();
    private ArrayList<DownloadListener> downloadListeners = new ArrayList<>();

    public static final HttpDownloader getInstance() {
        return httpDownloader;
    }

    public Charset getHttpContentEncoding(HttpURLConnection con, Charset defaultCharset) {
        String encoding;
        if ((encoding = con.getContentEncoding()) == null)
            return defaultCharset;

        return Charset.forName(encoding);
    }

    public HttpURLConnection establishHttpUrlConnection(URL url) throws IOException {
        HttpURLConnection gitConnectURL = ((HttpURLConnection) url.openConnection());
        return gitConnectURL;
    }

    public ReadableByteChannel createReadableByteChannel(HttpURLConnection in) throws IOException {
        return Channels.newChannel(new BufferedInputStream(in.getInputStream()));
    }

    public ByteBuffer readContent(URL url) throws IOException {
        HttpURLConnection downloadConnection = establishHttpUrlConnection(url);

        ReadableByteChannel readableByteChannel = createReadableByteChannel(downloadConnection);
        ByteBuffer byteBuffer = ByteBuffer.allocate(downloadConnection.getContentLength());
        int bytesRead = 0;
        while (bytesRead < byteBuffer.capacity()) {
            bytesRead += readableByteChannel.read(byteBuffer);
            notifyListeners(url, bytesRead, byteBuffer.capacity());
        }
        readableByteChannel.close();
        byteBuffer.flip();
        return byteBuffer;
    }

    public void readContent(URL url, Path path) throws IOException {
        if (Files.exists(path))
            Files.delete(path);

        HttpURLConnection downloadConnection = establishHttpUrlConnection(url);

        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.READ, StandardOpenOption.WRITE);
        ReadableByteChannel readableByteChannel = createReadableByteChannel(downloadConnection);
        int size = downloadConnection.getContentLength();
        int bytesMoved = 0;
        while (bytesMoved < size) {
            bytesMoved = (int) fileChannel.transferFrom(readableByteChannel, bytesMoved, size);
            notifyListeners(url, bytesMoved, size);
            fileChannel.force(false);
        }
        fileChannel.close();
    }

    @Override
    public byte[] downloadHttpContent(URL url) {
        byte[] contentBytes = null;
        try {
            contentBytes = readContent(url).array();
        } catch (IOException e) {
            return contentBytes;
        }
        return contentBytes;
    }

    @Override
    public void downloadHttpContent(URL url, Path path) {
        try {
            readContent(url, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getContentLength(URL url) throws IOException {
        return establishHttpUrlConnection(url).getContentLengthLong();
    }

    public long getContentLength(HttpURLConnection con) throws IOException {
        con.connect();
        return con.getContentLengthLong();
    }

    public <T extends DownloadListener> void addDownloadListener(T downloadListener) {
        this.downloadListeners.add(downloadListener);
    }

    private final void notifyListeners(URL url, int totalBytes, int size) {
        for (DownloadListener listener : downloadListeners) {
            listener.contentDownloaded(url, totalBytes, size);
        }
    }
}
