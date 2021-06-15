package client.db;

public class UserData {
    private static String authToken;

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        UserData.authToken = authToken;
    }
}
