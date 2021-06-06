package config;

import middleware.Middleware;
import middleware.Router;

import java.util.ArrayList;

public class Config {
    private static ArrayList<Class<? extends Middleware>> middlewares = new ArrayList<>();

    public static void initiate() {
        //Initiate apps and middleware config
        RouterConfig.initiate();

        middlewares.add(Router.class);
    }

    public static ArrayList<Class<? extends Middleware>> getMiddlewares() {
        return middlewares;
    }
}
