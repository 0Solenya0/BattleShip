package server.middleware;

import server.handler.RequestHandler;
import shared.request.Packet;

import java.util.concurrent.ConcurrentHashMap;

public class Auth extends Middleware {
    private static ConcurrentHashMap<String, Integer> AuthTokens = new ConcurrentHashMap<>();

    public Auth(Packet req, RequestHandler p) {
        super(req, p);
    }

    @Override
    public Packet process() {
        Packet response;
        if (req.getOrNull("AuthToken") != null && AuthTokens.containsKey(req.getOrNull("AuthToken")))
            req.addData("userId", String.valueOf(AuthTokens.get(req.getOrNull("AuthToken"))));
        response = next();
        if (response == null)
            return null;
        String resAuth = response.getOrNull("AuthToken");
        if (resAuth != null && !AuthTokens.containsKey(resAuth))
            AuthTokens.put(resAuth, Integer.valueOf(response.getOrNull("userId")));
        return response;
    }
}
