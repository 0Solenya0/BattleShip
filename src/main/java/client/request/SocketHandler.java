package client.request;

import client.request.exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.lock.CustomLock;
import shared.request.Packet;
import shared.request.PacketListener;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class SocketHandler extends shared.handler.SocketHandler {
    private static final ConcurrentHashMap<Integer, PacketListener> ridListeners = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PacketListener> targetListener = new ConcurrentHashMap<>();
    private static final AtomicInteger lastRid = new AtomicInteger();
    private static final Logger logger = LogManager.getLogger(SocketHandler.class);
    private static final ReentrantLock serviceLock = new ReentrantLock();
    private static SocketHandler socketHandler;

    public static SocketHandler getSocketHandler() throws ConnectionException {
        serviceLock.lock();
        if (socketHandler == null) {
            try {
                return new SocketHandler(new Socket("localhost", 8080));
            } catch (IOException e) {
                logger.info("failed to open new connection with server");
                throw new ConnectionException();
            }
        }
        serviceLock.unlock();
        return socketHandler;
    }

    public SocketHandler(Socket socket) throws IOException {
        super(socket);
    }

    public void sendPacketAndListen(Packet packet, PacketListener packetListener) {
        int rid = lastRid.addAndGet(1);
        ridListeners.put(rid, packetListener);
        packet.addData("m-rid", rid);
        sendPacket(packet);
    }

    public Packet sendPacketAndGetResponse(Packet packet) {
        AtomicReference<Packet> response = new AtomicReference<>();
        CustomLock lock = new CustomLock();
        int rid = lastRid.addAndGet(1);
        packet.addData("m-rid", rid);
        lock.lock();
        ridListeners.put(rid, (p) -> {
            response.set(p);
            lock.unlock();
        });
        sendPacket(packet);
        lock.lock();
        return response.get();
    }

    public void addTargetListener(String target, PacketListener listener) {
        targetListener.put(target, listener);
    }

    @Override
    public void listenPacket(Packet packet) {
        if (packet.hasKey("m-rid"))
            ridListeners.get(packet.getInt("m-rid")).listenPacket(packet);
        if (targetListener.containsKey(packet.target))
            targetListener.get(packet.target).listenPacket(packet);
    }
}
