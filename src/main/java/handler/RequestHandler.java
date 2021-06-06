package handler;

import config.Config;
import middleware.AbstractMiddleware;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.Packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RequestHandler {
    private static final Logger logger = LogManager.getLogger(RequestHandler.class);
    private int curMid;
    private final Packet req;

    public RequestHandler(Packet req) {
        this.req = req;
        curMid = -1;
        next();
    }

    public Packet next() {
        curMid++;
        if (curMid == Config.getMiddlewares().size())
            return null;
        Class<? extends AbstractMiddleware> MiddlewareClass = Config.getMiddlewares().get(curMid);
        try {
            Constructor<? extends AbstractMiddleware> constructor = MiddlewareClass
                    .getConstructor(Packet.class, RequestHandler.class);
            return constructor.newInstance(req, this).process();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.fatal("failed to instantiate and run middleware - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
