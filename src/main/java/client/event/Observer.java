package client.event;

public interface Observer<T> {
    void update(T value);
}
