package client.controller;

import shared.event.ObservableField;
import client.request.SocketHandler;
import client.request.exception.ConnectionException;
import shared.model.Board;
import shared.request.Packet;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class GameController {
    private static int BOARD_SIZE = 10; // TO DO add config
    private int gameId;
    public final ObservableField<Integer> playerNumber, turn;
    public final ObservableField<String> p1Name, p2Name;
    private ArrayList<ObservableField<Board.Cell>> boards;
    public ObservableField<Integer> refreshBoard;
    public final ObservableField<Boolean> started, finalBoard;
    public final ObservableField<LocalTime> timeout;
    private int boardRid;

    public GameController() {
        playerNumber = new ObservableField<>();
        p1Name = new ObservableField<>();
        p2Name = new ObservableField<>();
        turn = new ObservableField<>();
        boards = new ArrayList<>();
        started = new ObservableField<>();
        started.set(false);
        finalBoard = new ObservableField<>();
        timeout = new ObservableField<>();
        refreshBoard = new ObservableField<>();
        refreshBoard.set(0);
        for (int i = 0; i < 2 * BOARD_SIZE * BOARD_SIZE; i++)
            boards.add(new ObservableField<>());
    }

    public void newGame() throws ConnectionException {
        Packet packet = new Packet("pool");
        SocketHandler.getSocketHandler().sendPacket(packet);
        SocketHandler.getSocketHandler().addTargetListener("new-game", this::setupGame);
    }

    public void setupGame(Packet packet) {
        gameId = packet.getInt("game-id");
        playerNumber.set(packet.getInt("player-id"));
        started.set(true);
        Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException())
                .addTargetListener("new-board", this::getNewBoard);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("set-board", this::setFinalBoard);
    }

    public void setFinalBoard(Packet packet) {
        short[][] board = packet.getObject("board", short[][].class);
        finalBoard.set(true);
        refreshBoard.set(-1);
        timeout.set(LocalTime.now());
    }

    public void getNewBoard(Packet packet) {
        Board board = packet.getObject("board", Board.class);
        timeout.set(packet.getObject("timeout", LocalTime.class));
        System.out.println(timeout.get());
        boardRid = packet.getInt("rid");
        setBoard(board);
    }

    public void setBoard(Board board) {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                setBoardCell(playerNumber.get(), i, j, board.getCell(i, j));
    }

    public void acceptBoard() {
        Packet packet = new Packet("game");
        refreshBoard.set(-1);
        packet.put("rid", boardRid);
        packet.put("accept", "true");
        Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException()).sendPacket(packet);
    }

    public void rejectBoard() {
        Packet packet = new Packet("game");
        refreshBoard.set(refreshBoard.get() + 1);
        packet.put("rid", boardRid);
        packet.put("accept", "false");
        Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException()).sendPacket(packet);
    }

    private void setBoardCell(int playerNumber, int row, int col, Board.Cell value) {
        boards.get(playerNumber * BOARD_SIZE * BOARD_SIZE + row * BOARD_SIZE + col).set(value);
    }

    public ObservableField<Board.Cell> getBoardCell(int playerNumber, int row, int col) {
        return boards.get(playerNumber * BOARD_SIZE * BOARD_SIZE + row * BOARD_SIZE + col);
    }
}
