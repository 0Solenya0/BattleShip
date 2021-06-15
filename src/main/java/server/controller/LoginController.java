package server.controller;

import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.model.User;
import server.middleware.Auth;
import shared.request.StatusCode;
import shared.request.Packet;

public class LoginController extends Controller {
    Context context = new Context();

    @Override
    public Packet respond(Packet req) throws ConnectionException {
        String username = req.getOrNull("username");
        String password = req.getOrNull("password");
        if (username == null || password == null)
            return new Packet(StatusCode.BAD_REQUEST);
        Packet response = new Packet(StatusCode.OK);
        User user = context.users.getFirst(u -> u.getUsername().equals(username));
        if (user != null && user.checkPassword(password)) {
            Auth.addToken("123", user.id);
            return response.addData("auth-token", "123")
                    .addData("user-id", String.valueOf(user.id)); // TO DO Handle AuthToken
        }
        return response.addData("error", "Username or password is wrong!");
    }
}