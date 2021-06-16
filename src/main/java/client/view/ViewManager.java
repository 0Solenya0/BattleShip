package client.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import shared.util.Config;

import java.io.IOException;
import java.util.Objects;

public class ViewManager extends Application {
    private static final Logger logger = LogManager.getLogger(ViewManager.class);
    private final Config config = Config.getConfig("mainConfig");

    private static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        window = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource(config.getProperty("MAIN_MENU_VIEW"))));
        Pane pane = fxmlLoader.load();

        setScene(new Scene(pane));
    }

    public static void setScene(Scene scene) {
        window.setScene(scene);
        window.show();
    }

    public static Stage getWindow() {
        return window;
    }
}