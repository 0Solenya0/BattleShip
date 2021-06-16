package client.view;

import client.view.AbstractView;
import javafx.fxml.Initializable;
import shared.request.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuView extends AbstractView implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //new Thread(this::joinPool).start();
        //new Thread(this::joinPool).start();
    }

    public void joinPool() {
        try {
            Socket socket = new Socket("localhost", 8080);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Packet packet = new Packet("pool");
            packet.addData("auth-token", "123");
            outputStream.writeObject(packet);
        }
        catch (Exception ignored) {

        }
    }
}
