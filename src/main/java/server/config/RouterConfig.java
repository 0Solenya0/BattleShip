package server.config;

import server.controller.*;
import server.game.GameController;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RouterConfig {
    private static ArrayList<Route> routes = new ArrayList<>();

    public static void initiate() {
        routes.add(new Route("score-board", ScoreBoardController.class));
        routes.add(new Route("user-profile", UserProfileController.class));
        routes.add(new Route("login", LoginController.class));
        routes.add(new Route("register", RegistrationController.class));
        routes.add(new Route("pool", PlayerPoolController.class));
        routes.add(new Route("game.*", GameController.class));
    }

    public static ArrayList<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        private final Pattern pattern;
        private final Class<? extends Controller> target;

        public Route(String regex, Class<? extends Controller> target) {
            this.pattern = Pattern.compile(regex);
            this.target = target;
        }

        public boolean isMatched(String url) {
            if (url == null)
                return false;
            return pattern.matcher(url).matches();
        }

        public Class<? extends Controller> getTarget() {
            return target;
        }
    }
}
