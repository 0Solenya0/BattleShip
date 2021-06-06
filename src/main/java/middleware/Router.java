package middleware;

import config.RouteConfig;
import handler.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.Packet;

import java.lang.reflect.InvocationTargetException;

public class Router extends AbstractMiddleware {
    private static final Logger logger = LogManager.getLogger(Router.class);
    public Router(Packet req, RequestHandler p) {
        super(req, p);
    }

    @Override
    public Packet process() {
        for (RouteConfig.Route route: RouteConfig.getRoutes())
            if (route.isMatched(req.target)) {
                try {
                    return route.getTarget().getConstructor().newInstance().respond(req);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.fatal("view doesn't have the intended methods - "
                            + req.target + " - "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        // No routes matched returning null
        return next();
    }
}
