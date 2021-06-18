package client.view;

import client.controller.GameController;
import client.controller.GameListController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import shared.game.GameData;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameListView extends AbstractView implements Initializable {

    @FXML
    private VBox vboxList;

    GameListController gameListController;

    @FXML
    void btnMainMenuClicked(ActionEvent event) {
        ViewManager.goToMenu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameListController = new GameListController();
        gameListController.games.addObserver(this::updateList);
    }

    private void updateList(HashMap<Integer, GameData> games) {
        Platform.runLater(() -> {
            vboxList.getChildren().clear();
            games.forEach((gId, game) -> {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource(config.getProperty("GAME_CARD"))));
                try {
                    Pane pane = fxmlLoader.load();
                    GameCard gameCard = fxmlLoader.getController();
                    gameCard.update(game);
                    gameCard.setOnClickListener(() -> {
                        gameListController.stopFetch();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(Objects.requireNonNull(getClass().getResource(config.getProperty("GAME_VIEW"))));
                        try {
                            Pane p = loader.load();
                            GameController g = new GameController();
                            g.observeGame(gId);
                            GameView gameView = loader.getController();
                            gameView.setGameControllerForObserver(g);

                            ViewManager.setScene(new Scene(p));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    vboxList.getChildren().add(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
