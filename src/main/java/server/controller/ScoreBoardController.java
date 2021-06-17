package server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.db.Context;
import server.db.DBSet;
import server.db.exception.ConnectionException;
import server.db.exception.ValidationException;
import server.db.model.User;
import shared.request.Packet;
import shared.request.StatusCode;

import java.util.ArrayList;
import java.util.Comparator;

public class ScoreBoardController extends Controller {
    private static Context context = new Context();

    public static Packet respond(Packet req) throws ConnectionException {
        ArrayList<User> users = context.users.getAll(user -> true);
        users.sort(Comparator.comparingInt(u -> (u.getWins() - u.getLoses())));
        ArrayList<Integer> list = new ArrayList<>();
        for (User user: users)
            list.add(user.id);
        Packet response = new Packet(StatusCode.OK);
        response.addObject("user-list", list);
        return response;
    }
}
