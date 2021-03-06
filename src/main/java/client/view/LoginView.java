package client.view;

import client.controller.AuthenticationController;
import client.request.exception.ConnectionException;
import client.request.exception.ResponseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginView extends AbstractView implements Initializable {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblUsername, lblPassword, lblErr;

    @FXML
    private Button btnLogin, btnRegister;

    AuthenticationController authenticationController = new AuthenticationController();

    @FXML
    void btnRegisterClicked(ActionEvent event) throws IOException {
        ViewManager.goToRegister();
    }

    @FXML
    void loginBtnClicked(ActionEvent event) throws ConnectionException {
        try {
            authenticationController.login(txtUsername.getText(), txtPassword.getText());
            ViewManager.goToMenu();
            System.out.println("logged in successfully");
        } catch (ResponseException e) {
            lblErr.setVisible(true);
            lblErr.setText(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblErr.setVisible(false);
    }
}
