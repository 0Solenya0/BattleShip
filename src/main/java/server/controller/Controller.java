package server.controller;

import server.db.exception.ConnectionException;
import shared.request.Packet;

public abstract class Controller {
    public static Packet respond(Packet req) throws ConnectionException {
        return null;
    }
}
