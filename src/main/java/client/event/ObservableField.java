package client.event;

import java.util.ArrayList;

public class ObservableField<T> {
    private final ArrayList<Observer<T>> observers = new ArrayList<>();
    private T value;

    public synchronized void addObserver(Observer<T> observer) {
        observers.add(observer);
        if (value != null)
            observer.update(value);
    }

    public synchronized void notifyObservers() {
        for (Observer<T> observer: observers)
            observer.update(value);
    }

    public synchronized void set(T value) {
        this.value = value;
        notifyObservers();
    }

    public synchronized T get() {
        return value;
    }
}
