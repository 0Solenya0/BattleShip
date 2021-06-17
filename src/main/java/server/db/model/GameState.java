package server.db.model;

public class GameState extends Model {
    private short[][][] boards = new short[2][10][10];
    private int player1Id, player2Id, turn;

    public void setCell(int playerNumber, int x, int y, short state) {
        boards[playerNumber][x][y] = state;
    }

    public short getCell(int playerNumber, int x, int y) {
        return boards[playerNumber][x][y];
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public short[][][] getBoards() {
        return boards;
    }

    public int getTurn() {
        return turn;
    }

    public void setBoard(int plId, short[][] board) {
        this.boards[plId] = board;
    }
}
