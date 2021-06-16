package server.config;

import server.middleware.*;

import java.util.ArrayList;

public class Config {
    private static ArrayList<Class<? extends Middleware>> middlewares = new ArrayList<>();

    public static void initiate() {
        //Initiate apps and server.middleware server.config
        RouterConfig.initiate();
        middlewares.add(ClientRID.class);
        middlewares.add(Auth.class);
        middlewares.add(ServerRID.class);
        middlewares.add(Router.class);
    }

    public static ArrayList<Class<? extends Middleware>> getMiddlewares() {
        return middlewares;
    }
}
