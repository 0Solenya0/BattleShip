package server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.request.Packet;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private static AtomicInteger clientIDS = new AtomicInteger();
    private static ConcurrentHashMap<Integer, Client> clientMap = new ConcurrentHashMap<>();
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final int id;

    public Client(Socket socket) throws IOException {
        id = clientIDS.addAndGet(1);
        clientMap.put(id, this);
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        Thread clientThread = new Thread(this::requestListener);
        clientThread.start();
    }

    public static Client getClient(int id) {
        return clientMap.get(id);
    }

    private void requestListener() {
        while (true) {
            try {
                Packet packet = (Packet) inputStream.readObject();
                System.out.println("Got server.request - " + packet.target);
                packet.addData("client", String.valueOf(id));
                RequestHandler requestHandler = new RequestHandler(packet);
                Packet response = requestHandler.next();
                if (response != null)
                    sendResponse(response);
            }
            catch (EOFException e) {
                break;
            }
            catch (Exception e) {
                logger.error("invalid server.request was made -" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendResponse(Packet response) {
        try {
            outputStream.writeObject(response);
        } catch (IOException e) {
            logger.error("failed to send response to user - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
