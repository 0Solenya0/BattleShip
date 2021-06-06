package game;

import handler.Client;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class PlayerPool {
    private final ConcurrentLinkedQueue<Client> players;
    private final Lock lock;

    public PlayerPool() {
        lock = new ReentrantLock();
        players = new ConcurrentLinkedQueue<>();
        Thread matcher = new Thread(this::matchPlayers);
        matcher.start();
    }

    public void addPlayer(Client client) {
        lock.lock();
        players.add(client);
        lock.unlock();
    }

    public void removePlayer(Client client) {
        lock.lock();
        players.remove(client);
        lock.unlock();
    }

    public void matchPlayers() {
        while (true) {
            if (players.size() >= 2) {
                lock.lock();
                while (players.size() >= 2) {
                    Player player1 = new Player(players.poll());
                    Player player2 = new Player(players.poll());
                    new GameController(player1, player2);
                }
                lock.unlock();
            }
        }
    }
}
