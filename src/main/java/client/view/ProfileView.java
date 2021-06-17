package client.view;

import client.controller.UserController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileView extends AbstractView {

    @FXML
    private Label lblUsername, lblScore, lblWins, lblLoses;

    private UserController userController;

    public void setUser(int userId) {
        userController = new UserController(userId);
        userController.username.addObserver(this::updateUsername);
        userController.wins.addObserver(this::updateWins);
        userController.loses.addObserver(this::updateLoses);
    }

    private void updateUsername(String s) {
        Platform.runLater(() -> {
            lblUsername.setText(s);
        });
    }

    private void updateScore() {
        lblScore.setText(String.valueOf(userController.wins.get() - userController.loses.get()));
    }

    private void updateWins(Integer integer) {
        Platform.runLater(() -> {
            lblWins.setText(String.valueOf(integer));
            updateScore();
        });
    }

    private void updateLoses(Integer integer) {
        Platform.runLater(() -> {
            lblLoses.setText(String.valueOf(integer));
            updateScore();
        });
    }

    @FXML
    void btnMainMenuClicked(ActionEvent event) {
        ViewManager.goToMenu();
    }
}
