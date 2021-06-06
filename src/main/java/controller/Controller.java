package controller;

import db.exception.ConnectionException;
import request.Packet;

public abstract class Controller {
    public abstract Packet respond(Packet req) throws ConnectionException;
}
