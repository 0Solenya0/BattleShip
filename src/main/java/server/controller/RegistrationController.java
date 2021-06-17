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

public class RegistrationController extends Controller {
    private static final Logger logger = LogManager.getLogger(RegistrationController.class);
    private static Context context = new Context();

    public static Packet respond(Packet req) throws ConnectionException {
        String username = req.getOrNull("username");
        String password = req.getOrNull("password");
        Packet response = new Packet(StatusCode.OK);
        User user = new User(username, password);
        try {
            context.users.save(user);
        } catch (ValidationException e) {
            return response.put("error", "validation").addObject("validation", e);
        }
        logger.info("new user created - " + req.getOrNull("username"));
        System.out.println("new user registered - " + req.getOrNull("username"));
        return new Packet(StatusCode.CREATED);
    }
}
