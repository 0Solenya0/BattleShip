package shared.lock;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomLock {
    private final Object object = new Object();
    private final AtomicBoolean locked;

    public CustomLock() {
        locked = new AtomicBoolean();
    }

    public void lock() throws InterruptedException {
        synchronized (object) {
            while (true) {
                if (locked.get())
                    object.wait();
                else
                    break;
            }
            locked.set(true);
        }
    }

    public void unlock() {
        synchronized (object) {
            locked.set(false);
            object.notifyAll();
        }
    }
}
