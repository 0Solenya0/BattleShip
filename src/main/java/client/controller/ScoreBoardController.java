package client.controller;

import client.request.SocketHandler;
import com.google.gson.reflect.TypeToken;
import shared.request.Packet;

import java.util.ArrayList;
import java.util.Objects;

public class ScoreBoardController {

    public static ArrayList<Integer> getScoreboardUsers() {
        Packet packet = new Packet("score-board");
        Packet response = Objects.requireNonNull(SocketHandler.getSocketHandlerWithoutException())
                .sendPacketAndGetResponse(packet);
        return response.getObject("user-list", new TypeToken<ArrayList<Integer>>(){}.getType());
    }
}
