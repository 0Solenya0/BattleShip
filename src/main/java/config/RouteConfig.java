package config;

import view.AbstractView;
import view.FirstView;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RouteConfig {
    private static ArrayList<Route> routes = new ArrayList<>();

    public static void initiate() {
        routes.add(new Route("mani", FirstView.class));
    }

    public static ArrayList<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        private final Pattern pattern;
        private final Class<? extends AbstractView> target;

        public Route(String regex, Class<? extends AbstractView> target) {
            this.pattern = Pattern.compile(regex);
            this.target = target;
        }

        public boolean isMatched(String url) {
            return pattern.matcher(url).matches();
        }

        public Class<? extends AbstractView> getTarget() {
            return target;
        }
    }
}
