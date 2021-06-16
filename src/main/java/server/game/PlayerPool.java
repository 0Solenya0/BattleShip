package server.game;

import server.handler.SocketHandler;
import shared.lock.CustomLock;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class PlayerPool {
    private static final CustomLock serviceLock = new CustomLock();
    private static PlayerPool playerPool;
    private final LinkedList<Player> players;
    private final ReentrantLock lock = new ReentrantLock();

    public static PlayerPool getPlayerPool() {
        serviceLock.lock();
        if (playerPool == null)
            playerPool = new PlayerPool();
        serviceLock.unlock();
        return playerPool;
    }

    public PlayerPool() {
        players = new LinkedList<>();
    }

    public void addPlayer(SocketHandler socketHandler, int userid) {
        removePlayer(socketHandler);// TO DO remove with respect to userid
        lock.lock();
        System.out.println("added new player to pool");
        Player player = new Player(socketHandler, userid);
        players.add(player);
        System.out.println(players.size());
        while (players.size() >= 2) {
            Player player1 = players.poll();
            Player player2 = players.poll();
            player1.setPlayerNumber(0);
            player2.setPlayerNumber(1);
            new GameController(player1, player2);
        }
        lock.unlock();
    }

    public void removePlayer(SocketHandler socketHandler) {
        lock.lock();
        for (Player player: players)
            if (player.getSocketHandler().equals(socketHandler)) {
                players.remove(player);
                System.out.println("removed player from pool");
                break;
            }
        lock.unlock();
    }
}
