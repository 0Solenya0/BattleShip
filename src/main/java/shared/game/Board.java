package shared.game;

public class Board {
    private static final int SIZE = 10; // TO DO add config
    private static short[] dx = new short[]{0, 0, -1, -1, -1, 1, 1, 1};
    private static short[] dy = new short[]{1, -1, -1, 0, 1, -1, 0, 1};

    private transient short[][] boardShips;
    Cell[][] board;

    public Board(short[][] board) {
        this.board = new Cell[SIZE][SIZE];
        boardShips = new short[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) {
                boardShips[i][j] = board[i][j];
                if (board[i][j] > 0)
                    this.board[i][j] = Cell.SHIP;
                else
                    this.board[i][j] = Cell.EMPTY;
            }
    }

    public Board() {
        board = new Cell[SIZE][SIZE];
        boardShips = new short[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = Cell.EMPTY;
                boardShips[i][j] = 0;
            }
    }

    public void copy(Board board) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                this.board[i][j] = board.getCell(i, j);
    }

    boolean isValid(int r, int c) {
        return c >= 0 && r >= 0 && r < SIZE && c < SIZE;
    }

    private synchronized boolean checkAdjacent(int x, int y, short shipNum) {
        for (int i = 0; i < dx.length; i++)
            for (int j = 0; j < dx.length; j++) {
                if (isValid(x + dx[i], y + dy[j]) &&
                        boardShips[x + dx[i]][y + dy[j]] == shipNum)
                    return true;
            }
        return false;
    }

    private synchronized void bombAdjacent(int x, int y) {
        boolean bomb = true;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (boardShips[x][y] == boardShips[i][j] && board[i][j].equals(Cell.SHIP))
                    bomb = false;
        if (bomb) {
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    if (checkAdjacent(i, j, boardShips[x][y]))
                        bomb(i, j);
        }
    }

    public synchronized boolean bomb(int x, int y) {
        if (getCell(x, y) == Cell.SHIP) {
            setCell(x, y, Cell.HIT);
            bombAdjacent(x, y);
            return true;
        }
        else if (getCell(x, y) == Cell.EMPTY)
            setCell(x, y, Cell.MISS);
        return false;
    }

    public synchronized boolean isAllHit() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] == Cell.SHIP)
                    return false;
        return true;
    }

    public synchronized void hideShips() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] == Cell.SHIP)
                    board[i][j] = Cell.EMPTY;
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
