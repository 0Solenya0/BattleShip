package server.db;

import server.db.model.GameState;
import server.db.model.User;

public class Context {
    public DBSet<User> users = new DBSet<User>(User.class);
    public DBSet<GameState> gameStates = new DBSet<GameState>(GameState.class);
}
