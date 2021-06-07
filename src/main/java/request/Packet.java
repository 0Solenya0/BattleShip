package request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import db.gsonAdapter.LocalDateAdapter;
import db.gsonAdapter.LocalDateTimeAdapter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Packet implements Serializable {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public String target;
    public int status;
    private final HashMap<String, String> data = new HashMap<>();

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

    public <T> Packet addObject(String key, T obj) {
        data.put(key, gson.toJson(obj));
        return this;
    }

    public <T> T getObject(String key, Class<T> objClass) {
        return gson.fromJson(data.get(key), objClass);
    }
}
