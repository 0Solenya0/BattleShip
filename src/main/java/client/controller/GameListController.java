package client.controller;

import client.request.SocketHandler;
import com.google.gson.reflect.TypeToken;
import shared.event.ObservableField;
import shared.game.GameData;
import shared.request.Packet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameListController {
    public ObservableField<HashMap<Integer, GameData>> games;
    private Timer timer = new Timer();

    public GameListController() {
        games = new ObservableField<>();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateList();
            }
        }, 0, 3000);
    }

    private void updateList() {
        Packet packet = new Packet("game-list");
        Packet res = Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException())
                .sendPacketAndGetResponse(packet);
         games.set(res.getObject("games", new TypeToken<HashMap<Integer, GameData>>(){}.getType()));
    }
}
