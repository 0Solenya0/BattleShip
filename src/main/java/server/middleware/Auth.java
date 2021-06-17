package server.middleware;

import server.handler.RequestHandler;
import shared.request.Packet;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

public class Auth extends Middleware {
    private static final ConcurrentHashMap<String, Integer> authTokens = new ConcurrentHashMap<>();

    public static String registerUser(int userId) {
        SecureRandom secureRandom = new SecureRandom();
        String tokenId = String.valueOf(secureRandom.nextLong());
        authTokens.put(tokenId, userId);
        return tokenId;
    }

    public Auth(Packet req, RequestHandler p) {
        super(req, p);
    }

    @Override
    public Packet process() {
        if (req.hasKey("auth-token")) {
            String token = req.getOrNull("auth-token");
            if (authTokens.containsKey(token))
                req.put("user-id", String.valueOf(authTokens.get(token)));
        }
        return next();
    }
}
