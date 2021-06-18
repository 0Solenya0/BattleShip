package client.controller;

import shared.event.ObservableField;
import client.request.SocketHandler;
import client.request.exception.ConnectionException;
import shared.game.Board;
import shared.game.GameData;
import shared.request.Packet;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class GameController {
    private static int BOARD_SIZE = 10; // TO DO add config
    private int gameId;
    public final ObservableField<Integer> playerNumber, turn, round, refreshBoard, winner, viewersCnt;
    public final ObservableField<String> p1Name, p2Name;
    private ArrayList<ObservableField<Board.Cell>> boards;
    public final ObservableField<Boolean> started, finalBoard, gameOver;
    public final ObservableField<LocalTime> timeout;
    private boolean observer;
    private int boardRid;

    public GameController() {
        playerNumber = new ObservableField<>();
        p1Name = new ObservableField<>();
        p2Name = new ObservableField<>();
        turn = new ObservableField<>();
        round = new ObservableField<>();
        winner = new ObservableField<>();
        viewersCnt = new ObservableField<>();
        gameOver = new ObservableField<>();
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

    public void observeGame(int gameId) {
        observer = true;
        Packet packet = new Packet("game-data");
        packet.put("game-id", gameId);
        Packet res = Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException())
                .sendPacketAndGetResponse(packet);
        p1Name.set(res.getObject("data", GameData.class).p1Name);
        p2Name.set(res.getObject("data", GameData.class).p2Name);

        packet = new Packet("game-observe");
        packet.put("game-id", gameId);
        Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException())
                .sendPacket(packet);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("game-data", this::updateState);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("end-turn", this::updateTurn);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("game-over", this::gameOver);
    }

    public void newGame() throws ConnectionException {
        Packet packet = new Packet("pool");
        SocketHandler.getSocketHandler().sendPacket(packet);
        SocketHandler.getSocketHandler().addTargetListener("new-game", this::setupGame);
    }

    public void setupGame(Packet packet) {
        gameId = packet.getInt("game-id");
        playerNumber.set(packet.getInt("player-id"));
        p1Name.set(packet.getOrNull("username-p1"));
        p2Name.set(packet.getOrNull("username-p2"));
        started.set(true);
        Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException())
                .addTargetListener("new-board", this::getNewBoard);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("set-board", this::setFinalBoard);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("game-data", this::updateState);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("end-turn", this::updateTurn);
        SocketHandler.getSocketHandlerWithoutException()
                .addTargetListener("game-over", this::gameOver);
    }

    public void gameOver(Packet packet) {
        winner.set(packet.getInt("winner"));
        gameOver.set(true);
    }

    public void setFinalBoard(Packet packet) {
        Board board = packet.getObject("board", Board.class);
        finalBoard.set(true);
        refreshBoard.set(-1);
        timeout.set(LocalTime.now());
    }

    public void getNewBoard(Packet packet) {
        Board board = packet.getObject("board", Board.class);
        timeout.set(packet.getObject("timeout", LocalTime.class));
        System.out.println("timeout: " + timeout.get());
        boardRid = packet.getInt("rid");
        setBoard(playerNumber.get(), board);
    }

    private void updateTurn(Packet packet) {
        // Turn finished sent from server
    }

    private void updateState(Packet packet) {
        setBoard(0, packet.getObject("board-p1", Board.class));
        setBoard(1, packet.getObject("board-p2", Board.class));
        turn.set(packet.getInt("turn"));
        round.set(packet.getInt("round"));
        timeout.set(packet.getObject("timeout", LocalTime.class));
        viewersCnt.set(packet.getInt("viewer-cnt"));
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

    public boolean isObserver() {
        return observer;
    }

    public void playTurn(int row, int col) {
        Packet packet = new Packet("game-play-turn");
        packet.put("game-id", gameId);
        packet.put("round", round.get());
        packet.put("row", row);
        packet.put("col", col);
        Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException()).sendPacket(packet);
    }

    public void setBoard(int playerNumber, Board board) {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                setBoardCell(playerNumber, i, j, board.getCell(i, j));
    }

    private void setBoardCell(int playerNumber, int row, int col, Board.Cell value) {
        boards.get(playerNumber * BOARD_SIZE * BOARD_SIZE + row * BOARD_SIZE + col).set(value);
    }

    public ObservableField<Board.Cell> getBoardCell(int playerNumber, int row, int col) {
        return boards.get(playerNumber * BOARD_SIZE * BOARD_SIZE + row * BOARD_SIZE + col);
    }
}
