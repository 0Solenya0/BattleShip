package server.controller;

import server.db.exception.ConnectionException;
import server.game.PlayerPool;
import server.handler.SocketHandler;
import shared.request.Packet;
import shared.request.StatusCode;

public class PlayerPoolController extends Controller {
    @Override
    public Packet respond(Packet req) throws ConnectionException {
        int clientId = Integer.parseInt(req.getOrNull("handler"));
        SocketHandler socketHandler = SocketHandler.getSocketHandler(clientId);
        if (!req.hasKey("user-id"))
            return new Packet("pool").addData("error", "user is not logged in");
        PlayerPool.getPlayerPool().addPlayer(socketHandler, Integer.parseInt(req.getOrNull("user-id")));
        return new Packet(StatusCode.OK);
    }
}
