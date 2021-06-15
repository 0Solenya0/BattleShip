package server.middleware;

import server.handler.RequestHandler;
import shared.request.Packet;

import java.util.concurrent.ConcurrentHashMap;

public class Auth extends Middleware {
    private static ConcurrentHashMap<String, Integer> authTokens = new ConcurrentHashMap<>();

    public static void addToken(String tokenId, int userId) {
        authTokens.put(tokenId, userId);
    }

    public Auth(Packet req, RequestHandler p) {
        super(req, p);
    }

    @Override
    public Packet process() {
        if (req.hasKey("auth-token")) {
            String token = req.getOrNull("auth-token");
            if (authTokens.containsKey(token))
                req.addData("user-id", String.valueOf(authTokens.get(token)));
        }
        return next();
    }
}
