package server.game;

import shared.model.Board;

import java.util.Random;

public class BoardBuilder {
    private static int SIZE = 10; // TO DO get from server.config
    private static short[] dx = new short[]{0, 0, -1, -1, -1, 1, 1, 1};
    private static short[] dy = new short[]{1, -1, -1, 0, 1, -1, 0, 1};
    private short[][] board = new short[SIZE][SIZE];

    public BoardBuilder() {
    }

    public void clear() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = 0;
    }

    public BoardBuilder randomBoat(int len, int val) {
        while(true) {
            Random random = new Random();
            boolean rev = random.nextBoolean();
            int r = random.nextInt(SIZE);
            int c = random.nextInt(SIZE);
            if (canAdd(r, c, len, rev)) {
                addBoat(r, c, len, rev, (short) val);
                break;
            }
        }
        return this;
    }

    public void addBoat(int r, int c, int len, boolean rev, short val) {
        for (int i = 0; i < len; i++) {
            if (!rev)
                board[r + i][c] = val;
            if (rev)
                board[r][c + i] = val;
        }
    }

    boolean isValid(int r, int c) {
        return c >= 0 && r >= 0 && r < SIZE && c < SIZE;
    }

    boolean checkCell(int r, int c) {
        if (!isValid(r, c) || board[r][c] != 0)
            return false;
        for (int i = 0; i < dx.length; i++) {
            int x = r + dx[i], y = c + dy[i];
            if (isValid(x, y) && board[x][y] != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean canAdd(int r, int c, int len, boolean rev) {
        if ((!rev && r + len >= SIZE) || (rev && c + len >= SIZE))
            return false;
        for (int i = 0; i < len; i++) {
            if (!rev && !checkCell(r + i, c))
                return false;
            if (rev && !checkCell(r, c + i))
                return false;
        }
        return true;
    }

    public Board getBoard() {
        return new Board(board);
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                System.out.print(board[i][j] + " ");
            System.out.println();
        }
    }
}
