package server.middleware;

import server.handler.RequestHandler;
import server.request.ridListener;
import shared.request.Packet;
import shared.request.PacketListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerRID extends Middleware {
    private static final ConcurrentHashMap<Integer, ridListener> ridListeners = new ConcurrentHashMap<>();
    private static final AtomicInteger lastRid = new AtomicInteger();

    public static int registerListener(ridListener listener) {
        int rid = lastRid.addAndGet(1);
        ridListeners.put(rid, listener);
        return rid;
    }

    public ServerRID(Packet req, RequestHandler p) {
        super(req, p);
    }

    @Override
    public Packet process() {
        // TO DO invalid requests
        if (req.hasKey("rid"))
            return ridListeners.get(req.getInt("rid")).listenPacket(req);
        return next();
    }
}
