package client.db;

public class UserData {
    private static String authToken;
    private static int userId;

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        UserData.authToken = authToken;
    }

    public static void setUserId(int uId) {
        userId = uId;
    }

    public static int getUserId() {
        return userId;
    }
}
