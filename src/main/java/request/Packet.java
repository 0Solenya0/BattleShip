package request;

import java.io.Serializable;
import java.util.HashMap;

public class Packet implements Serializable {
    public String target;
    public int status;
    private HashMap<String, String> data = new HashMap<>();

    public Packet() {

    }

    public Packet(StatusCode statusCode) {
        status = statusCode.getCode();
    }

    public String getOrNull(String key) {
        if (data.containsKey(key))
            return data.get(key);
        return null;
    }

    public Packet addData(String key, String value) {
        data.put(key, value);
        return this;
    }
}
