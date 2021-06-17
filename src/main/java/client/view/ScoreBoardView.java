package client.view;

import client.controller.ScoreBoardController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ScoreBoardView extends AbstractView implements Initializable {

    @FXML
    private VBox vboxPlayers;

    @FXML
    void btnMainMenuClicked(ActionEvent event) {
        ViewManager.goToMenu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Integer> users = ScoreBoardController.getScoreboardUsers();
        for (int i = 0; i < users.size(); i++) {
            int id = users.get(i);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("USER_CARD"))));
            try {
                Pane pane = fxmlLoader.load();
                UserCard card = fxmlLoader.getController();
                card.setUser(id);
                card.setOnClickListener(() -> {
                    ViewManager.goToProfile(id);
                });
                vboxPlayers.getChildren().add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
