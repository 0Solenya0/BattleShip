import request.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Test {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8080);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        Packet packet = new Packet();
        packet.target = "mani";
        outputStream.writeObject(packet);
    }
}
