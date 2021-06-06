package view;

import request.Packet;

public abstract class AbstractView {
    public abstract Packet respond(Packet req);
}
