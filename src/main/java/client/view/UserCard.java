package client.view;

import client.controller.UserController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserCard {

    @FXML
    private Label lblUsername, lblOnline, lblWins, lblLooses;

    private UserController userController;
    private Runnable onClickListener;

    public void setUser(int userId) {
        userController = new UserController(userId);
        userController.username.addObserver(this::updateUsername);
        userController.wins.addObserver(this::updateWins);
        userController.loses.addObserver(this::updateLoses);
        userController.online.addObserver(this::updateOnline);
    }

    public void setOnClickListener(Runnable runnable) {
        onClickListener = runnable;
    }

    @FXML
    void cardClicked(ActionEvent event) {
        onClickListener.run();
    }

    private void updateUsername(String s) {
        Platform.runLater(() -> {
            lblUsername.setText(s);
        });
    }

    private void updateWins(Integer integer) {
        Platform.runLater(() -> {
            lblWins.setText(String.valueOf(integer));
        });
    }

    private void updateLoses(Integer integer) {
        Platform.runLater(() -> {
            lblLooses.setText(String.valueOf(integer));
        });
    }

    private void updateOnline(Boolean bool) {
        Platform.runLater(() -> {
            if (bool) {
                lblOnline.getStyleClass().remove("offline_user");
                lblOnline.getStyleClass().add("online_user");
            }
            else {
                lblOnline.getStyleClass().add("offline_user");
                lblOnline.getStyleClass().remove("online_user");
            }
        });
    }

}
