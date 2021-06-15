package server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.request.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketHandler extends shared.handler.SocketHandler {
    private static final Logger logger = LogManager.getLogger(server.handler.SocketHandler.class);
    private static ConcurrentHashMap<Integer, SocketHandler> clientMap = new ConcurrentHashMap<>();
    private static AtomicInteger clientIDS = new AtomicInteger();
    private final int id;

    public SocketHandler(Socket socket) throws IOException {
        super(socket);
        id = clientIDS.addAndGet(1);
        clientMap.put(id, this);
    }

    public static SocketHandler getSocketHandler(int id) {
        return clientMap.get(id);
    }

    protected void requestListener() {
        while (true) {
            try {
                Packet packet = (Packet) inputStream.readObject();
                System.out.println("Got request - " + packet.target);
                packet.addData("handler", String.valueOf(id));
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
}
