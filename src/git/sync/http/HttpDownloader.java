package git.sync.http;

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
        return gitConnectURL;
    }

    public ReadableByteChannel createReadableByteChannel(HttpURLConnection in) throws IOException {
        return Channels.newChannel(in.getInputStream());
    }

    public ByteBuffer readChannel(ReadableByteChannel channel,int size) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        int bytesRead = 0;
        while(bytesRead < size){
            bytesRead += channel.read(byteBuffer);
        }
        channel.close();
        byteBuffer.flip();
        return byteBuffer;
    }

    public void readChannel(ReadableByteChannel readableByteChannel, Path path, long size) throws IOException {
        if (Files.exists(path))
            Files.delete(path);

        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.READ, StandardOpenOption.WRITE);
        long bytesTransfered = 0;
        while (bytesTransfered < size) {
            bytesTransfered = fileChannel.transferFrom(readableByteChannel, bytesTransfered, size);
            fileChannel.force(false);
        }
        fileChannel.close();
    }

    @Override
    public byte[] downloadHttpContent(URL url) {
        byte[] contentBytes = null;
        try {
            HttpURLConnection gitConnectURL = establishHttpUrlConnection(url);
            contentBytes = readChannel(createReadableByteChannel(gitConnectURL), gitConnectURL.getContentLength()).array();
        } catch (IOException e) {
            return contentBytes;
        }
        return contentBytes;
    }

    public long getContentLength(URL url) throws IOException {
        return establishHttpUrlConnection(url).getContentLengthLong();
    }

    public long getContentLength(HttpURLConnection con) throws IOException {
        con.connect();
        return con.getContentLengthLong();
    }

    @Override
    public void downloadHttpContent(URL url, Path path) {
        try {
            HttpURLConnection con = establishHttpUrlConnection(url);
            ReadableByteChannel readableByteChannel = createReadableByteChannel(con);
            readChannel(readableByteChannel, path, con.getContentLengthLong());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
