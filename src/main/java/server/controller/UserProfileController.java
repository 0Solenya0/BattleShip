package server.controller;

import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.model.User;
import server.middleware.Auth;
import shared.request.Packet;
import shared.request.StatusCode;

public class UserProfileController extends Controller {
    private static final Context context = new Context();

    public static Packet respond(Packet req) throws ConnectionException {
        Packet response = new Packet(StatusCode.OK);
        if (!req.hasKey("id"))
            return new Packet(StatusCode.BAD_REQUEST);
        User user = context.users.get(req.getInt("id"));
        response.put("username", user.getUsername());
        response.put("wins", user.getWins());
        response.put("loses", user.getLoses());
        response.put("online", Auth.isUserOnline(user.id));
        return response;
    }
}
