package config;

import controller.Controller;
import controller.FirstController;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RouterConfig {
    private static ArrayList<Route> routes = new ArrayList<>();

    public static void initiate() {
        routes.add(new Route("mani", FirstController.class));
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
            return pattern.matcher(url).matches();
        }

        public Class<? extends Controller> getTarget() {
            return target;
        }
    }
}
