package client;

import client.view.ViewManager;

import java.io.IOException;

public class run {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /*Packet packet = new Packet();
        Socket socket = new Socket("localhost", 8080);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        packet.target = "login";
        packet.addData("username", "mani");
        packet.addData("password", "mani");
        objectOutputStream.writeObject(packet);
        Packet packet1 = (Packet) objectInputStream.readObject();*/
        ViewManager.main(args);
    }
}
