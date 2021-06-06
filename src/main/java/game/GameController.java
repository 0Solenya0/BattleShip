package game;

import db.model.GameState;

public class GameController {
    private GameState gameState;
    private Player[] players = new Player[2];

    public GameController(Player player1, Player player2) {
        GameState gameState = new GameState();
        players[0] = player1;
        players[1] = player2;
    }
}
