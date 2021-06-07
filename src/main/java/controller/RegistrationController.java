package controller;

import db.Context;
import db.exception.ConnectionException;
import db.exception.ValidationException;
import db.model.User;
import request.Packet;
import request.StatusCode;

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
