package server.controller;

import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.exception.ValidationException;
import server.db.model.User;
import shared.request.Packet;
import shared.request.StatusCode;

public class RegistrationController extends Controller {
    Context context = new Context();

    @Override
    public Packet respond(Packet req) throws ConnectionException {
        String username = req.getOrNull("username");
        String password = req.getOrNull("password");
        Packet response = new Packet(StatusCode.OK);
        User user = new User(username, password);
        try {
            context.users.save(user);
        } catch (ValidationException e) {
            return response.addData("error", "validation").addObject("validation", e);
        }
        System.out.println("ok");
        return new Packet(StatusCode.CREATED);
    }
}
