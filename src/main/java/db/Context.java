package db;

import db.dbset.DBSet;
import db.exception.ConnectionException;
import db.exception.ValidationException;
import db.model.User;

public class Context {
    public DBSet<User> users = new DBSet<User>(User.class);
}
