package view;

import request.Packet;

public abstract class View {
    public abstract Packet respond(Packet req);
}
