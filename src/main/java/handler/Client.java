package handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.Packet;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Thread clientThread;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        clientThread = new Thread(this::requestListener);
        clientThread.start();
    }

    private void requestListener() {
        while (true) {
            try {
                Packet packet = (Packet) inputStream.readObject();
                System.out.println("Got request - " + packet.target);
                RequestHandler requestHandler = new RequestHandler(packet);
            }
            catch (EOFException e) {
                break;
            }
            catch (Exception e) {
                logger.error("invalid request was made -" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

}
