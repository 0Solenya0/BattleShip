package client.view;

import client.controller.GameController;
import client.request.exception.ConnectionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class MainMenuView extends AbstractView {

    @FXML
    private Button btnNewGame, btnScoreboard, btnProfile;

    @FXML
    void btnNewGameClicked(ActionEvent event) throws IOException, ConnectionException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource(config.getProperty("GAME_VIEW"))));
        Pane pane = fxmlLoader.load();
        GameController g = new GameController();
        g.newGame();
        GameView gameView = fxmlLoader.getController();
        gameView.addGameController(g);

        ViewManager.setScene(new Scene(pane));
    }

    @FXML
    void btnProfileClicked(ActionEvent event) {
    }

    @FXML
    void btnScoreboardClicked(ActionEvent event) {
        ViewManager.goToScoreBoard();
    }
}
