package shared.model;

public class Board {
    private static final int SIZE = 10; // TO DO add config
    Cell[][] board;

    public Board(short[][] board) {
        this.board = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] > 0)
                    this.board[i][j] = Cell.SHIP;
                else
                    this.board[i][j] = Cell.EMPTY;
    }

    public Board() {
        board = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = Cell.EMPTY;
    }

    public void bomb(int x, int y) {
        if (getCell(x, y) == Cell.SHIP)
            setCell(x, y, Cell.HIT);
        else if (getCell(x, y) == Cell.EMPTY)
            setCell(x, y, Cell.MISS);
    }

    public synchronized void setCell(int x, int y, Cell val) {
        board[x][y] = val;
    }

    public synchronized Cell getCell(int x, int y) {
        return board[x][y];
    }

    public enum Cell {
        SHIP,
        HIT,
        MISS,
        EMPTY
    }
}
