package client.view;

import client.controller.GameController;
import client.controller.GameListController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import shared.game.Board;
import shared.util.Config;

import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class GameView implements Initializable {
    private static final Config config = Config.getConfig("mainConfig");
    private static final int DIM = Integer.parseInt(config.getProperty("BOARD_SIZE"));
    private static final int BOARD_SIZE = Integer.parseInt(config.getProperty("GRID_PANE_DIM"));

    @FXML
    private GridPane gridpaneP1, gridpaneP2;

    @FXML
    private Button btnAccept, btnReject, btnMainMenu;

    @FXML
    private Label lblP2, lblP1, lblHead, lblTimer, lblViewerCnt;

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

    public void cellClicked(int gridId, int row, int col) {
        System.out.println(gridId + " " + row + " " + col + " cell clicked");
        if (gameController.playerNumber.get() != gridId
                && gameController.getBoardCell(gridId, row, col).get().equals(Board.Cell.EMPTY)) {
            gameController.playTurn(row, col);
        }
    }

    public void setGameControllerForObserver(GameController gameController) {
        Platform.runLater(() -> {
            btnMainMenu.setVisible(true);
            lblHead.setText("waiting for players to set boards...");
            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < DIM; i++)
                    for (int j = 0; j < DIM; j++) {
                        Rectangle tile = new Rectangle(BOARD_SIZE / DIM, BOARD_SIZE / DIM);
                        tile.setFill(Color.TRANSPARENT);
                        StackPane pane = new StackPane(tile);
                        getGrid(k).add(pane, j, i);
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
            gameController.viewersCnt.addObserver(this::updateViewers);
            gameController.gameOver.addObserver(this::gameOver);
            gameController.turn.addObserver(this::updateTurn);
            gameController.round.addObserver(this::updateHead);
            gameController.timeout.addObserver(this::updateTimer);
        });
    }

    private void updateViewers(Integer integer) {
        Platform.runLater(() -> {
            lblViewerCnt.setText(String.valueOf(integer));
        });
    }

    public void setGameControllerForPlayer(GameController gameController) {
        this.gameController = gameController;
        System.out.println("added observer");
        gameController.started.addObserver(this::startGame);
    }

    public void startGame(Boolean start) {
        System.out.println(start);
        Platform.runLater( () -> {
            if (!start) {
                lblHead.setText("Searching for an opponent");
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
            gameController.viewersCnt.addObserver(this::updateViewers);
            gameController.gameOver.addObserver(this::gameOver);
            gameController.turn.addObserver(this::updateTurn);
            gameController.round.addObserver(this::updateHead);
            gameController.refreshBoard.addObserver(this::updateBtn);
            gameController.timeout.addObserver(this::updateTimer);
        });
    }

    private void updateHead(Integer round) {
        Platform.runLater(() -> {
            lblHead.setText("Round " + round);
        });
    }

    private void gameOver(Boolean isOver) {
        if (!isOver)
            return;
        Platform.runLater(() -> {
            timer.cancel();
            timer.purge();
            lblTimer.setText("");
            if (gameController.winner.get() == 0)
                lblHead.setText(gameController.p1Name.get() + " has won the match!");
            else
                lblHead.setText(gameController.p2Name.get() + " has won the match!");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ViewManager.goToMenu();
                }
            }, 5000);
        });
    }

    Timer timer = new Timer();
    private void updateTimer(LocalTime localTime) {
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
                    else
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

    public void updateCell(Board.Cell value, StackPane pane) {
        Platform.runLater(() -> {
            Rectangle tile = (Rectangle) pane.getChildren().get(0);
            if (pane.getChildren().size() > 1)
                pane.getChildren().remove(1);
            Label label = new Label();
            label.getStyleClass().add("board_cell_obj");
            pane.getChildren().add(label);
            switch (value) {
                case SHIP -> {
                    label.setText("C");
                    label.getStyleClass().add("board_ship_cell");
                }
                case HIT -> {
                    label.setText("r");
                    label.getStyleClass().add("board_hit_cell");
                }
                case MISS -> {
                    label.setText("z");
                    label.getStyleClass().add("board_miss_cell");
                }
                case EMPTY -> {
                    pane.getChildren().remove(label);
                }
            }
        });
    }

    public void updateUserLabel(int playerNumber, String username) {
        Platform.runLater(() -> {
            getPlayerLabel(playerNumber).setText(username);
        });
    }

    private boolean hasStyleClass(ObservableList<String> classes, String target) {
        for (String s: classes)
            if (s.equals(target))
                return true;
        return false;
    }

    public void updateTurn(int turn) {
        Platform.runLater( () -> {
            if (!hasStyleClass(getActivatePlayerLabel(turn).getStyleClass(), "active_icon")) {
                getActivatePlayerLabel(turn).getStyleClass().add("active_icon");
                getActivatePlayerLabel(1 - turn).getStyleClass().add("deactive_icon");
                getActivatePlayerLabel(turn).getStyleClass().remove("deactive_icon");
                getActivatePlayerLabel(1 - turn).getStyleClass().remove("active_icon");
            }
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

    @FXML
    void btnMainMenuClicked(ActionEvent event) {
        ViewManager.goToMenu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnReject.setVisible(false);
        btnAccept.setVisible(false);
        btnMainMenu.setVisible(false);
    }
}
