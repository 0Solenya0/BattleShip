package shared.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.handler.RequestHandler;
import shared.request.Packet;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SocketHandler {
    private static final Logger logger = LogManager.getLogger(SocketHandler.class);
    protected final Socket socket;
    protected final ObjectInputStream inputStream;
    protected final ObjectOutputStream outputStream;

    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        Thread clientThread = new Thread(this::requestListener);
        clientThread.start();
    }

    protected abstract void requestListener();

    public synchronized void sendResponse(Packet response) {
        try {
            outputStream.writeObject(response);
        } catch (IOException e) {
            logger.error("failed to send response to user - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
