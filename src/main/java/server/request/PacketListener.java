package server.request;

import shared.request.Packet;

public interface PacketListener {
    void listen(Packet packet);
}
