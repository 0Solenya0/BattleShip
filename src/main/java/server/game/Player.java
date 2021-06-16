package server.game;

import server.handler.SocketHandler;
import shared.request.Packet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player {
    private SocketHandler socketHandler;
    private final AtomicBoolean ready;
    private final int userId;
    private int playerNumber;

    public Player(SocketHandler socketHandler, int userId) {
        this.socketHandler = socketHandler;
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

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }

    public boolean isReady() {
        return ready.get();
    }

    public void setReady(boolean value) {
        ready.set(value);
    }

    public void sendPacket(Packet packet) {
        socketHandler.sendPacket(packet);
    }

    public void addOnDisconnectListener(Runnable runnable) {
        socketHandler.addListener(runnable);
    }
}
