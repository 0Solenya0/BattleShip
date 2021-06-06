package config;

import middleware.AbstractMiddleware;
import middleware.Router;

import java.util.ArrayList;

public class Config {
    private static ArrayList<Class<? extends AbstractMiddleware>> middlewares = new ArrayList<>();

    public static void initiate() {
        //Initiate apps and middleware config
        RouteConfig.initiate();

        middlewares.add(Router.class);
    }

    public static ArrayList<Class<? extends AbstractMiddleware>> getMiddlewares() {
        return middlewares;
    }
}
