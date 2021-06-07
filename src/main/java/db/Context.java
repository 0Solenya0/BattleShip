package db;

import db.model.User;

public class Context {
    public DBSet<User> users = new DBSet<User>(User.class);
}
