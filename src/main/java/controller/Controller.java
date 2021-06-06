package controller;

import request.Packet;

public abstract class Controller {
    public abstract Packet respond(Packet req);
}
