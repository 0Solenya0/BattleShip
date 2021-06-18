package client.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import shared.game.GameData;
import shared.util.Config;

public class GameCard {
    private static final Config config = Config.getConfig("mainConfig");
    private static final int SHIP_CNT = Integer.parseInt(config.getProperty("SHIP_CNT"));

    @FXML
    private Label lblHeader, lblDestroyedP1, lblHitP1, lblDestroyedP2, lblHitP2, lblRounds;

    private Runnable onClickListener;

    public void setOnClickListener(Runnable listener) {
        onClickListener = listener;
    }

    public void update(GameData gameData) {
        lblHeader.setText(gameData.p1Name + " vs " + gameData.p2Name);
        lblDestroyedP1.setText(gameData.p1HitShips + "/" + SHIP_CNT);
        lblDestroyedP2.setText(gameData.p2HitShips + "/" + SHIP_CNT);
        lblHitP1.setText(String.valueOf(gameData.p1HitTargets));
        lblHitP2.setText(String.valueOf(gameData.p2HitTargets));
        if (gameData.round == -1)
            lblRounds.setText("starting...");
        else
            lblRounds.setText(String.valueOf(gameData.round));
    }

    @FXML
    void cardClicked(ActionEvent event) {
        onClickListener.run();
    }

}
