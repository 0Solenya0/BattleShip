package client.controller;

import client.db.UserData;
import client.request.SocketHandler;
import client.request.exception.ConnectionException;
import client.request.exception.ResponseException;
import client.request.exception.ValidationException;
import shared.request.Packet;

public class AuthenticationController {
    public void login(String username, String password) throws ConnectionException, ResponseException {
        Packet packet = new Packet("login");
        packet.put("username", username);
        packet.put("password", password);
        Packet response = SocketHandler.getSocketHandler().sendPacketAndGetResponse(packet);
        String authToken = response.getOrNull("auth-token");
        if (authToken == null)
            throw new ResponseException(response.getOrNull("error"));
        UserData.setAuthToken(authToken);
    }

    public void register(String username, String password) throws ValidationException, ConnectionException {
        Packet packet = new Packet("register");
        packet.put("username", username);
        packet.put("password", password);
        Packet response = SocketHandler.getSocketHandler().sendPacketAndGetResponse(packet);
        if (response.status != 201)
            throw response.getObject("validation", ValidationException.class);
    }
}
