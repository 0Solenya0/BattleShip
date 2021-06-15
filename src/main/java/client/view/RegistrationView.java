package client.view;

import client.Controller.AuthenticationController;
import client.request.exception.ConnectionException;
import client.request.exception.ValidationException;
import client.view.AbstractView;
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

public class RegistrationView extends AbstractView implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblUsername, lblPassword;

    @FXML
    private Button btnLogin, btnRegister;

    @FXML
    private Label lblErr;

    AuthenticationController authenticationController = new AuthenticationController();

    @FXML
    void btnLoginClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource(config.getProperty("LOGIN_VIEW"))));
        Pane pane = fxmlLoader.load();

        ViewManager.setScene(new Scene(pane));
    }

    @FXML
    void btnRegisterClicked() throws ConnectionException {
        try {
            authenticationController.register(txtUsername.getText(), txtPassword.getText(), () -> {
                try {
                    btnLoginClicked();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (ValidationException e) {
            lblErr.setVisible(true);
            lblErr.setText(e.getAllErrors().get(0));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblErr.setVisible(false);
    }
}
