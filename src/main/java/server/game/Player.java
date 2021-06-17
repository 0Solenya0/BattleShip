package server.game;

import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.model.User;
import server.handler.SocketHandler;
import shared.event.ObservableField;
import shared.request.Packet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player {
    private SocketHandler socketHandler;
    private final ObservableField<Boolean> ready;
    private final int userId;
    private int playerNumber;
    private Context context = new Context();

    public Player(SocketHandler socketHandler, int userId) {
        this.socketHandler = socketHandler;
        ready = new ObservableField<>();
        ready.set(false);
        this.userId = userId;
    }

    public User getUser() {
        try {
            return context.users.get(userId);
        } catch (ConnectionException e) {
            System.out.println("couldn't get username - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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

    public ObservableField<Boolean> getReady() {
        return ready;
    }

    public void sendPacket(Packet packet) {
        socketHandler.sendPacket(packet);
    }

    public void addOnDisconnectListener(Runnable runnable) {
        socketHandler.addDisconnectListener(runnable);
    }
}
