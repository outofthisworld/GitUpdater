package Updater.Git;

import Json.Git.GitParseListener;
import Updater.Exception.ProjectRevisionException;
import Updater.Updater;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Unknown on 5/01/2016.
 */
public abstract class GitUpdater extends Updater {
    private final String GIT_API_URL;
    private GitParseListener parseListener;
    private GitUpdateDetails updateDetails;

    public <T extends GitUpdateDetails,U extends GitParseListener> GitUpdater(T gitUpdateDetails,U parseListener) throws IOException {
        this.updateDetails = gitUpdateDetails;
        this.parseListener = parseListener;
        GIT_API_URL = String.format("https://api.github.com/repos/%s/%s/commits?", updateDetails.getGitUser(), updateDetails.getRepo());
    }

    public final <T extends GitUpdateDetails> void setUpdateDetails(T updateDetails) throws IOException {
        this.updateDetails = updateDetails;
    }
    
    protected HttpURLConnection establishHttpUrlConnection(URL url) throws IOException {
        HttpURLConnection gitConnectURL = ((HttpURLConnection) url.openConnection());
        gitConnectURL.connect();
        return gitConnectURL;
    }

    protected ReadableByteChannel createReadableByteChannel(HttpURLConnection in) throws IOException {
        return Channels.newChannel(in.getInputStream());
    }

    protected ByteBuffer readChannel(ReadableByteChannel channel,int size) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        channel.read(byteBuffer);
        channel.close();
        return byteBuffer;
    }

    protected Charset getHttpContentEncoding(HttpURLConnection con,Charset defaultCharset){
        String encoding;
        if((encoding = con.getContentEncoding()) == null)
            return defaultCharset;

        return Charset.forName(encoding);
    }

    public <T extends GitParseListener> void setParseListener(T listener){
        this.parseListener = listener;
    }

    @Override
    public String getLatestProjectRevision() throws ProjectRevisionException {
        try {
            HttpURLConnection gitConnectURL = establishHttpUrlConnection(
                    new URL(GIT_API_URL + updateDetails.createGetQueryString()));
           String jsonString = new String(
                    readChannel(createReadableByteChannel(gitConnectURL), gitConnectURL.getContentLength()).array(),
                    getHttpContentEncoding(gitConnectURL,StandardCharsets.UTF_8)
            );
            parseListener.parseResponse(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProjectRevisionException("Could not determine latest project revision, an IO exception occurred whilst trying to retrieve the revision");
        }
        return parseListener.getLatestRevision();
    }

    @Override
    public String downloadLatestRevision() {
        return "C:\\Users\\Unknown\\Desktop\\testFile.jpeg";
    }
}
