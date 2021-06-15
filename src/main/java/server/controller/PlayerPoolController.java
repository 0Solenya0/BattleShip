package server.controller;

import server.db.exception.ConnectionException;
import server.game.PlayerPool;
import server.handler.Client;
import shared.request.Packet;

public class PlayerPoolController extends Controller {
    @Override
    public Packet respond(Packet req) throws ConnectionException {
        int clientId = Integer.parseInt(req.getOrNull("client"));
        Client client = Client.getClient(clientId);
        if (!req.hasKey("user-id"))
            return new Packet("pool").addData("error", "user is not logged in");
        PlayerPool.getPlayerPool().addPlayer(client, Integer.parseInt(req.getOrNull("user-id")));
        return null;
    }
}
