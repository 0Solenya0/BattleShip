package shared.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.game.Player;
import server.handler.RequestHandler;
import shared.lock.CustomLock;
import shared.request.Packet;
import shared.request.PacketListener;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SocketHandler implements PacketListener {
    private static final Logger logger = LogManager.getLogger(SocketHandler.class);

    protected final Socket socket;
    protected final ObjectInputStream inputStream;
    protected final ObjectOutputStream outputStream;
    private final ReentrantLock outputStreamLock = new ReentrantLock();

    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        Thread clientThread = new Thread(this::inputListener);
        clientThread.start();
    }

    private void inputListener() {
        while (true) {
            try {
                Packet packet = (Packet) inputStream.readObject();
                System.out.println("Got packet - " + packet.target);
                listenPacket(packet);
            }
            catch (EOFException e) {
                break;
            }
            catch (Exception e) {
                logger.error("invalid server.server.request was made -" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(Packet packet) {
        outputStreamLock.lock();
        try {
            outputStream.writeObject(packet);
        } catch (IOException e) {
            logger.error("failed to send response to user - " + e.getMessage());
            e.printStackTrace();
        }
        outputStreamLock.unlock();
    }
}
