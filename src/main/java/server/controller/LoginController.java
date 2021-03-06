package server.controller;

import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.model.User;
import server.middleware.Auth;
import shared.request.StatusCode;
import shared.request.Packet;

public class LoginController extends Controller {
    private static Context context = new Context();

    public static Packet respond(Packet req) throws ConnectionException {
        String username = req.getOrNull("username");
        String password = req.getOrNull("password");
        if (username == null || password == null)
            return new Packet(StatusCode.BAD_REQUEST);
        Packet response = new Packet(StatusCode.OK);
        User user = context.users.getFirst(u -> u.getUsername().equals(username));
        if (user != null) {
            if (Auth.isUserOnline(user.id))
                return new Packet(StatusCode.BAD_REQUEST).put("error", "User is already logged in with another device");
            if (user.checkPassword(password)) {
                return response
                        .put("auth-token", Auth.registerUser(user.id, req.getInt("handler")))
                        .put("user-id", user.id);
            }
        }
        return new Packet(StatusCode.BAD_REQUEST).put("error", "Username or password is wrong!");
    }
}
