package server.db.model;

import shared.game.Board;

public class GameState extends DBModel {
    private Board[] boards = new Board[2];
    private int player1Id, player2Id, round = -1, turn;

    public synchronized int getPlayer1Id() {
        return player1Id;
    }

    public synchronized void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public synchronized int getPlayer2Id() {
        return player2Id;
    }

    public synchronized void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public synchronized Board[] getBoards() {
        return boards;
    }

    public synchronized void nextRound() {
        round++;
    }

    public synchronized int getRound() {
        return round;
    }

    public synchronized void reverseTurn() {
        turn = 1 - turn;
    }

    public synchronized int getTurn() {
        return turn;
    }

    public synchronized void setBoard(int plId, Board board) {
        this.boards[plId] = board;
    }

    public synchronized Board getBoard(int playerNumber) {
        return boards[playerNumber];
    }
}
