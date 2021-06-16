package server.util;

import server.handler.SocketHandler;
import server.middleware.ServerRID;
import shared.lock.CustomLock;
import shared.request.Packet;
import shared.request.StatusCode;

import java.util.concurrent.atomic.AtomicReference;

public class RidUtilities {
    public static Packet sendPacketAndGetResponse(Packet packet, SocketHandler socketHandler) throws InterruptedException {
        AtomicReference<Packet> response = new AtomicReference<>();
        CustomLock lock = new CustomLock();
        lock.lockIntrupted();
        int rid = ServerRID.registerListener((p) -> {
            response.set(p);
            lock.unlock();
            return new Packet(StatusCode.OK);
        });
        packet.addData("rid", rid);
        socketHandler.sendPacket(packet);
        lock.lockIntrupted();
        return response.get();
    }
}
