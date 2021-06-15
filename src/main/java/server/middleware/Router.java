package server.middleware;

import server.config.RouterConfig;
import server.db.exception.ConnectionException;
import server.handler.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.request.Packet;
import shared.request.StatusCode;

import java.lang.reflect.InvocationTargetException;

public class Router extends Middleware {
    private static final Logger logger = LogManager.getLogger(Router.class);
    public Router(Packet req, RequestHandler p) {
        super(req, p);
    }

    @Override
    public Packet process() {
        for (RouterConfig.Route route: RouterConfig.getRoutes())
            if (route.isMatched(req.target)) {
                try {
                    return route.getTarget().getConstructor().newInstance().respond(req);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.fatal("view doesn't have the intended methods - "
                            + req.target + " - "
                            + e.getMessage());
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    logger.error("Connection to database failed");
                    e.printStackTrace();
                    return new Packet(StatusCode.INTERNAL_SERVER_ERROR);
                }
            }
        // No routes matched returning null
        return next();
    }
}
