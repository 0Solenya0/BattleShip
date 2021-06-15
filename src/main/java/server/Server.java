package server;

import server.config.Config;
import server.handler.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        ExecutorService pool = Executors.newFixedThreadPool(10); // TO DO server.config
        Config.initiate();
        System.out.println("Ready for new connections");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("A client got connected");
            pool.execute(() -> {
                try {
                    new Client(socket);
                } catch (IOException e) {
                    logger.error("Connection with socket failed");
                    e.printStackTrace();
                }
            });
        }
    }
}
