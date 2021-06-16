package client.view;

import client.controller.GameController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class GameView {
    private static final int BOARD_SIZE = 450, DIM = 10; // TO DO add config

    @FXML
    private GridPane gridpaneP1, grifpaneP2;

    @FXML
    private Label lblP2, lblP1, lblHead;

    @FXML
    private Label lblActiveP1, lblActiveP2;

    private GameController gameController;

    public GridPane getGrid(int id) {
        if (id == 0)
            return gridpaneP1;
        return grifpaneP2;
    }

    public Label getPlayerLabel(int id) {
        if (id == 0)
            return lblP1;
        return lblP2;
    }

    public Label getActivatePlayerLabel(int id) {
        if (id == 0)
            return lblActiveP1;
        return lblActiveP2;
    }

    public void cellClicked(int gridId, int r, int col) {
        gameController.turn.set(1 - gameController.turn.get());
        System.out.println(gridId + " " + r + " " + col);
    }

    public void startGame(Boolean start) {
        if (!start)
            return;
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < DIM; i++)
                for (int j = 0; j < DIM; j++) {
                    Rectangle tile = new Rectangle(BOARD_SIZE / DIM, BOARD_SIZE / DIM);
                    tile.setFill(Color.TRANSPARENT);
                    getGrid(k).add(new StackPane(tile), j, i);
                    int ii = i, jj = j, kk = k;
                    tile.setOnMouseClicked((event) -> {
                        cellClicked(kk, ii, jj);
                    });
                }
        }
        gameController.p1Name.addObserver((s) -> {
            updateUserLabel(0, s);
        });
        gameController.p2Name.addObserver((s) -> {
            updateUserLabel(1, s);
        });
        gameController.turn.addObserver(this::updateTurn);
    }

    public void addGameController(GameController gameController) {
        this.gameController = gameController;
        gameController.started.addObserver(this::startGame);
    }

    public void updateUserLabel(int playerNumber, String username) {
        getPlayerLabel(playerNumber).setText(username);
    }

    public void updateTurn(int turn) {
        getActivatePlayerLabel(turn % 2).getStyleClass().add("active_icon");
        getActivatePlayerLabel(turn % 2).getStyleClass().remove("deactive_icon");
        getActivatePlayerLabel(1 - turn % 2).getStyleClass().add("deactive_icon");
        getActivatePlayerLabel(1 - turn % 2).getStyleClass().remove("active_icon");
    }
}
