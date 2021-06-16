package client.request;

import client.request.exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.request.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

public class SocketHandler extends shared.handler.SocketHandler {
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


    @Override
    protected void requestListener() {

    }
}
