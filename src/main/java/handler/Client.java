package handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.Packet;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        Thread clientThread = new Thread(this::requestListener);
        clientThread.start();
    }

    private void requestListener() {
        while (true) {
            try {
                Packet packet = (Packet) inputStream.readObject();
                System.out.println("Got request - " + packet.target);
                RequestHandler requestHandler = new RequestHandler(packet);
                sendResponse(requestHandler.next());
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

    private synchronized void sendResponse(Packet response) {
        try {
            outputStream.writeObject(response);
        } catch (IOException e) {
            logger.error("failed to send response to user - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
