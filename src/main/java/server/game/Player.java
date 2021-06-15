package server.game;

import server.handler.Client;
import shared.request.Packet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player {
    private Client client;
    private final int userId;
    private int playerNumber;
    private AtomicBoolean ready;

    public Player(Client client, int userId) {
        this.client = client;
        ready = new AtomicBoolean();
        this.userId = userId;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getId() {
        return userId;
    }

    public Client getClient() {
        return client;
    }

    public boolean isReady() {
        return ready.get();
    }

    public void setReady(boolean value) {
        ready.set(value);
    }

    public void sendPacket(Packet packet) {
        client.sendResponse(packet);
    }
}
