package server.game;

import java.util.Random;

public class BoardBuilder {
    private static int SIZE = 10; // TO DO get from server.config
    private short[][] board = new short[SIZE][SIZE];

    public BoardBuilder() {
    }

    public BoardBuilder randomBoat(int len, int val) {
        while (true) {
            Random random = new Random();
            int r = random.nextInt(10);
            int c = random.nextInt(10);
            boolean rev = random.nextBoolean();
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

    public boolean canAdd(int r, int c, int len, boolean rev) {
        // TO DO check no adjacent cell to have boat
        if ((!rev && r + len >= SIZE) || (rev && c + len >= SIZE))
            return false;
        for (int i = 0; i < len; i++) {
            if (!rev && board[r + i][c] != 0)
                return false;
            if (rev && board[r][c + i] != 0)
                return false;
        }
        return true;
    }

    public short[][] getBoard() {
        return board;
    }
}
