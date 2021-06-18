package server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.db.annotation.Unique;
import server.db.exception.ConnectionException;
import server.db.exception.ValidationException;
import shared.gsonAdapter.LocalDateAdapter;
import shared.gsonAdapter.LocalDateTimeAdapter;
import server.db.model.DBModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

public class DBSet<T extends DBModel> {
    private static final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    private static final Logger logger = LogManager.getLogger(DBSet.class);
    Class<T> modelClass;

    public DBSet(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    private String getDataSource() {
        return "server/db/" + modelClass.getName();
    }

    private int getLastId() {
        File path = new File(getDataSource() + "/");
        path.mkdirs();
        return path.list().length;
    }

    public T get(int id) throws ConnectionException {
        File path = new File(getDataSource() + "/" + id + ".json");
        try {
            FileReader file = new FileReader(path);
            if (!locks.containsKey(path.getPath()))
                locks.put(path.getPath(), new ReentrantReadWriteLock());

            locks.get(path.getPath()).readLock().lock();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            locks.get(path.getPath()).readLock().unlock();
            return gson.fromJson(file, modelClass);
        }
        catch (IOException e) {
            locks.get(path.getPath()).readLock().unlock();
            logger.error("Can't Read Database Record File - " + e.getMessage());
            throw new ConnectionException();
        }
    }

    public ArrayList<T> getAll(Predicate<T> query) throws ConnectionException {
        ArrayList<T> res = new ArrayList<>();
        for (int i = 1; i <= getLastId(); i++)
            if (query.test(get(i)))
                res.add(get(i));
        return res;
    }

    public T getFirst(Predicate<T> query) throws ConnectionException {
        for (int i = 1; i <= getLastId(); i++)
            if (query.test(get(i)))
                return get(i);
        return null;
    }

    public void delete(T model) throws ConnectionException {
        model.isDeleted = true;
        try {
            save(model);
        }
        catch (ValidationException exception) {
            logger.error("Unexpected validation error while deleting a model");
            return;
        }
        //logger.info("Model successfully deleted - " + modelClass.getName() + " - id: " + model.id);
    }

    public T save(T model) throws ConnectionException, ValidationException {
        //logger.info(String.format("Start saving model to database - An instance of %s with id %s is getting saved.", model.getClass(), model.id));

        if (!model.isDeleted)
            validate(model);

        if (model.id == 0) {
            model.id = getLastId() + 1;
            model.createdAt = LocalDateTime.now();
        }
        model.lastModified = LocalDateTime.now();

        File path = new File(getDataSource() + "/" + model.id + ".json");
        if (!locks.containsKey(path.getPath()))
            locks.put(path.getPath(), new ReentrantReadWriteLock());

        locks.get(path.getPath()).writeLock().lock();
        if (path.exists())
            path.delete();

        try {
            FileWriter writer = new FileWriter(getDataSource() + "/" + model.id + ".json");
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            gson.toJson(model, writer);
            writer.flush();
        }
        catch (IOException e) {
            logger.error("Database Record File Was Not Saved - " + e.getMessage());
            locks.get(path.getPath()).writeLock().unlock();
            throw new ConnectionException();
        }
        locks.get(path.getPath()).writeLock().unlock();

        //logger.info(String.format("An instance of %s with id %s got saved.", modelClass.getClass(), model.id));
        return model;
    }

    public void validate(T model) throws ConnectionException, ValidationException {
        ValidationException validationException = new ValidationException();
        for (Field field: modelClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Unique.class) != null) {
                T m = getFirst(obj -> {
                    if (obj.id == model.id)
                        return false;
                    try {
                        return field.get(obj) != null && field.get(obj).equals(field.get(model));
                    } catch (IllegalAccessException e) {
                        logger.fatal("validation failed, failed to access models field - " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                });
                if (m != null)
                    validationException.addError(field.getName(), field.getName() + " is not unique");
            }
        }
        if (validationException.hasError())
            throw new ValidationException();
    }
}