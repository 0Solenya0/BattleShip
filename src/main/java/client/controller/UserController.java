package client.controller;

import client.request.SocketHandler;
import shared.event.ObservableField;
import shared.request.Packet;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class UserController {
    private final int userId;
    public ObservableField<String> username;
    public ObservableField<Integer> wins, loses;
    public ObservableField<Boolean> online;
    private Timer refreshDataTimer = new Timer();

    public UserController(int pId) {
        username = new ObservableField<>();
        wins = new ObservableField<>();
        loses = new ObservableField<>();
        online = new ObservableField<>();
        userId = pId;
        refreshDataTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshData();
            }
        }, 0, 60000);
    }

    public void refreshData() {
        Packet packet = new Packet("user-profile");
        packet.put("id", userId);
        Packet response = Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException()).sendPacketAndGetResponse(packet);
        username.set(response.getOrNull("username"));
        wins.set(response.getInt("wins"));
        loses.set(response.getInt("loses"));
        online.set(response.getBool("online"));
    }
}
