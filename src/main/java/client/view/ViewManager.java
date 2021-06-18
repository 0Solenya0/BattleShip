package client.view;

import client.request.exception.ConnectionException;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import shared.util.Config;
import client.view.InfoDialog;

import java.io.IOException;
import java.util.Objects;

public class ViewManager extends Application {
    private static final Logger logger = LogManager.getLogger(ViewManager.class);
    private final static Config config = Config.getConfig("mainConfig");

    private static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Thread.currentThread().setUncaughtExceptionHandler(((thread, throwable) -> {
            if (throwable.getCause().getCause() instanceof ConnectionException)
                ViewManager.connectionError();
            else {
                logger.error("Unexpected error - " + throwable.getMessage());
                logger.trace(throwable);
                logger.trace(throwable.getCause());
            }
        }));
        window = primaryStage;
        goToLogin();
    }

    public static void loadPage(FXMLLoader fxmlLoader) {
        try {
            Pane pane = fxmlLoader.load();
            Platform.runLater(() -> {
                setScene(new Scene(pane));
            });
        } catch (IOException e) {
            logger.fatal("Failed to find fxml file - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void goToMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("MAIN_MENU_VIEW"))));
        loadPage(fxmlLoader);
    }

    public static void goToRegister() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("REGISTER_VIEW"))));
        loadPage(fxmlLoader);
    }

    public static void goToLogin() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("LOGIN_VIEW"))));
        loadPage(fxmlLoader);
    }

    public static void goToScoreBoard() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("SCORE_BOARD_VIEW"))));
        loadPage(fxmlLoader);
    }

    public static void goToProfile(int userId) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("PROFILE_VIEW"))));
        loadPage(fxmlLoader);
        ProfileView profileView = fxmlLoader.getController();
        profileView.setUser(userId);
    }

    public static void goToGameList() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(ViewManager.class.getResource(config.getProperty("GAME_LIST_VIEW"))));
        loadPage(fxmlLoader);
    }

    public static void setScene(Scene scene) {
        window.setScene(scene);
        window.show();
    }

    public static void connectionError() {
        InfoDialog.showFailed("Connection to server failed!\nPlease reload the program");
    }

    public static Stage getWindow() {
        return window;
    }
}