package client.controller;

import client.event.ObservableField;
import client.view.GameView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameController {
    private static int BOARD_SIZE = 10; // TO DO add config
    private int gameId;
    public final ObservableField<Integer> playerNumber, turn;
    public final ObservableField<String> p1Name, p2Name;
    private ArrayList<ObservableField<Short>> boards;
    private final Runnable onStartListener;

    public GameController(Runnable onStartListener) {
        playerNumber = new ObservableField<>();
        p1Name = new ObservableField<>();
        p2Name = new ObservableField<>();
        turn = new ObservableField<>();
        boards = new ArrayList<>();
        this.onStartListener = onStartListener;
    }

    public ObservableField<Short> addBoardObserver(int playerNumber, int row, int col) {
        return boards.get(playerNumber * BOARD_SIZE * BOARD_SIZE + row * BOARD_SIZE + col);
    }
}
