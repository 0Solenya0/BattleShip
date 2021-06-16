package client.view;

import client.controller.GameController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class GameView implements Initializable {
    private static final int BOARD_SIZE = 450, DIM = 10; // TO DO add config

    @FXML
    private GridPane gridpaneP1, gridpaneP2;

    @FXML
    private Button btnAccept, btnReject;

    @FXML
    private Label lblP2, lblP1, lblHead, lblTimer;

    @FXML
    private Label lblActiveP1, lblActiveP2;

    private GameController gameController;

    public GridPane getGrid(int id) {
        if (id == 0)
            return gridpaneP1;
        return gridpaneP2;
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
        System.out.println(gridId + " " + r + " " + col);
        // TO DO clicked
    }

    public void addGameController(GameController gameController) {
        this.gameController = gameController;
        System.out.println("added observer");
        gameController.started.addObserver(this::startGame);
    }

    public void startGame(Boolean start) {
        System.out.println(start);
        Platform.runLater( () -> {
            if (!start) {
                lblHead.setText("Searching for an opponent"); // TO DO add config
                return;
            }
            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < DIM; i++)
                    for (int j = 0; j < DIM; j++) {
                        Rectangle tile = new Rectangle(BOARD_SIZE / DIM, BOARD_SIZE / DIM);
                        tile.setFill(Color.TRANSPARENT);
                        StackPane pane = new StackPane(tile);
                        getGrid(k).add(pane, j, i);
                        int ii = i, jj = j, kk = k;
                        tile.setOnMouseClicked((event) -> {
                            cellClicked(kk, ii, jj);
                        });
                        gameController.getBoardCell(k, i, j).addObserver((value -> {
                            updateCell(value, pane);
                        }));
                    }
            }
            gameController.p1Name.addObserver((s) -> {
                updateUserLabel(0, s);
            });
            gameController.p2Name.addObserver((s) -> {
                updateUserLabel(1, s);
            });
            gameController.turn.addObserver(this::updateTurn);
            gameController.refreshBoard.addObserver(this::updateBtn);
            gameController.timeout.addObserver(this::updateHead);
        });
    }

    Timer timer = new Timer();
    private void updateHead(LocalTime localTime) {
        timer.purge();
        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    long s = Duration.between(LocalTime.now(), localTime).getSeconds();
                    if (s < 0) {
                        lblTimer.setText("");
                        timer.cancel();
                        timer.purge();
                    }
                    lblTimer.setText(String.valueOf(s));
                });
            }
        }, 500, 500);
    }

    public void updateBtn(int ref) {
        Platform.runLater(() -> {
            if (ref == -1 || ref >= 2) {
                btnAccept.setVisible(false);
                btnReject.setVisible(false);
            }
            if (ref >= 0 && ref < 2) {
                btnAccept.setVisible(true);
                btnReject.setVisible(true);
            }
        });
    }

    public void updateCell(short value, StackPane pane) {
        Platform.runLater(() -> {
            Rectangle tile = (Rectangle) pane.getChildren().get(0);
            if (value > 0)
                tile.setFill(Color.RED);
            else
                tile.setFill(Color.TRANSPARENT);
        });
    }

    public void updateUserLabel(int playerNumber, String username) {
        Platform.runLater(() -> {
            getPlayerLabel(playerNumber).setText(username);
        });
    }

    public void updateTurn(int turn) {
        Platform.runLater( () -> {
            getActivatePlayerLabel(turn % 2).getStyleClass().add("active_icon");
            getActivatePlayerLabel(turn % 2).getStyleClass().remove("deactive_icon");
            getActivatePlayerLabel(1 - turn % 2).getStyleClass().add("deactive_icon");
            getActivatePlayerLabel(1 - turn % 2).getStyleClass().remove("active_icon");
        });
    }

    @FXML
    void btnAcceptClicked(ActionEvent event) {
        gameController.acceptBoard();
    }

    @FXML
    void btnRejectClicked(ActionEvent event) {
        gameController.rejectBoard();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnReject.setVisible(false);
        btnAccept.setVisible(false);
    }
}
