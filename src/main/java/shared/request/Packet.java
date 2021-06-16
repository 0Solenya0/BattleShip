package shared.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import shared.gsonAdapter.LocalDateAdapter;
import shared.gsonAdapter.LocalDateTimeAdapter;
import shared.gsonAdapter.LocalTimeAdapter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class Packet implements Serializable {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();
    public String target = "";
    public int status;
    private final HashMap<String, String> data = new HashMap<>();

    public Packet(String target) {
        this.target = target;
    }

    public Packet(StatusCode statusCode) {
        status = statusCode.getCode();
    }

    public String getOrNull(String key) {
        if (data.containsKey(key))
            return data.get(key);
        return null;
    }

    public Packet put(String key, String value) {
        data.put(key, value);
        return this;
    }

    public Packet put(String key, int value) {
        data.put(key, String.valueOf(value));
        return this;
    }

    public boolean hasKey(String key) {
        return data.containsKey(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(data.get(key));
    }

    public <T> Packet addObject(String key, T obj) {
        data.put(key, gson.toJson(obj));
        return this;
    }

    public <T> T getObject(String key, Class<T> objClass) {
        return gson.fromJson(data.get(key), objClass);
    }

    public String getJson() {
        return gson.toJson(this);
    }
}
