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

public class PacketSender {
    private static final Logger logger = LogManager.getLogger(PacketSender.class);

    public static Packet sendPacket(Packet packet) throws ConnectionException {
        try {
            Socket socket = new Socket("localhost", 8080);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(packet);
            logger.info("packet sent");
            Packet response = (Packet) inputStream.readObject();
            socket.close();
            logger.info("packet received");
            return response;
        } catch (ClassNotFoundException e) {
            logger.error("server didn't send a packet - " + e.getMessage());
            throw new ConnectionException();
        } catch (UnknownHostException e) {
            logger.error("Unknown error - " + e.getMessage());
            e.printStackTrace();
            throw new ConnectionException();
        } catch (IOException e) {
            logger.error("package was not sent connection failed - " + e.getMessage());
            throw new ConnectionException();
        }
    }
}
