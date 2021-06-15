package client.controller;

import client.db.UserData;
import client.request.PacketSender;
import client.request.exception.ConnectionException;
import client.request.exception.ResponseException;
import client.request.exception.ValidationException;
import shared.request.Packet;

public class AuthenticationController {
    public void login(String username, String password, Runnable success) throws ConnectionException, ResponseException {
        Packet packet = new Packet("login");
        packet.addData("username", username);
        packet.addData("password", password);
        Packet response = PacketSender.sendPacket(packet);
        String authToken = response.getOrNull("AuthToken");
        if (authToken == null)
            throw new ResponseException(response.getOrNull("error"));
        UserData.setAuthToken(authToken);
        success.run();
    }

    public void register(String username, String password, Runnable success) throws ValidationException, ConnectionException {
        Packet packet = new Packet("register");
        packet.addData("username", username);
        packet.addData("password", password);
        Packet response = PacketSender.sendPacket(packet);
        if (response.status == 201)
            success.run();
        else
            throw response.getObject("validation", ValidationException.class);
    }
}
