package server.db.model;

import server.db.annotation.Unique;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class User extends DBModel {
    private static final Logger logger = LogManager.getLogger(User.class);

    @Unique
    private String username;
    private String password;
    private int wins, loses;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        loses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
