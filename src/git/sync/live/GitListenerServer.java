package git.sync.live;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by Unknown on 12/01/2016.
 */
public class GitListenerServer {


    public static void main(String[] args) {
        GitListenerServer gitListenerServer = new GitListenerServer();
        gitListenerServer.start();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(9090);
            Socket connected = serverSocket.accept();
            StringBuilder sb = new StringBuilder();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = connected.getChannel().read(byteBuffer);
                sb.append(byteBuffer.flip());
                byteBuffer.clear();
            }

            System.out.println(byteBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
