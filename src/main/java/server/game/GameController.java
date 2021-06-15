package server.game;

import server.controller.Controller;
import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.exception.ValidationException;
import server.db.model.GameState;
import server.handler.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.lock.CustomLock;
import shared.request.Packet;
import server.request.PacketListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class GameController extends Controller {
    private static int SET_BOARD_TTL = 30000, REFRESH_BOARD_TTL = 10000;
    private static final Logger logger = LogManager.getLogger(GameController.class);
    private static final ConcurrentHashMap<Integer, GameController> gameControllers = new ConcurrentHashMap<>();
    private GameState gameState;
    private Player[] players = new Player[2];
    private final ArrayList<Client> visitors = new ArrayList<>();
    private AtomicInteger rids = new AtomicInteger();
    private final ConcurrentHashMap<Integer, PacketListener> ridReq = new ConcurrentHashMap<>();
    Context context = new Context();

    public GameController(Player player1, Player player2) {
        System.out.println("New server.game created!!!!");
        this.gameState = new GameState();
        try {
            context.gameStates.save(gameState);
        } catch (ConnectionException e) {
            logger.error("couldn't connect to database");
        } catch (ValidationException e) {
            logger.fatal("error while creating an empty server.game state - " + e.getLog());
        }
        Packet packet = new Packet("new-server.game");
        packet.addObject("gameId", gameState.id);
        player1.sendPacket(packet);
        player2.sendPacket(packet);
        gameControllers.put(gameState.id, this);
        players[0] = player1;
        players[1] = player2;
        new Thread(() -> setBoards(player1)).start();
        new Thread(() -> setBoards(player2)).start();
    }

    public Packet getResponse(Packet req, Player player, boolean wait) throws InterruptedException {
        AtomicReference<Packet> response = new AtomicReference<>();
        int rid = rids.addAndGet(1);
        req.addData("rid", String.valueOf(rid));
        req.addData("server.game-id", String.valueOf(gameState.id));
        CustomLock lock = new CustomLock();

        if (wait)
            lock.lock();
        ridReq.put(rid, (p) -> {
            response.set(p);
            if (wait)
                lock.unlock();
        });
        player.sendPacket(req);
        if (wait)
            lock.lock();
        return response.get();
    }

    public void setBoards(Player player) {
        AtomicInteger time = new AtomicInteger(SET_BOARD_TTL);
        Timer timer = new Timer();
        Thread myThread = Thread.currentThread();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int t = time.addAndGet(-1000);
                if (t < 0)
                    myThread.interrupt();
            }
        }, 1000, 1000);
        BoardBuilder builder = new BoardBuilder();
        for (int i = 0; i < 3; i++) {
            builder.randomBoat(4, 1)
                    .randomBoat(3, 2)
                    .randomBoat(3, 3)
                    .randomBoat(2, 4)
                    .randomBoat(2, 5)
                    .randomBoat(2, 6)
                    .randomBoat(1, 7)
                    .randomBoat(1, 8)
                    .randomBoat(1, 9)
                    .randomBoat(1, 10);
            Packet packet = new Packet("new-board");
            packet.addObject("board", builder.getBoard());

            Packet response = null;
            try {
                response = getResponse(packet, player, true);
            } catch (InterruptedException e) {
                gameState.setBoard(player.getId(), builder.getBoard());
                player.setReady(true);
                logger.info("skipped user response in setting board");
                break;
            }
            assert response != null;
            System.out.println(response.getJson());
            if (response.getOrNull("accept").equals("true") || i == 2) {
                gameState.setBoard(player.getId(), builder.getBoard());
                player.setReady(true);
                break;
            }
            time.addAndGet(REFRESH_BOARD_TTL);
        }
        Packet packet = new Packet("set-board");
        packet.addObject("board", builder.getBoard());
        try {
            getResponse(packet, player, false);
        } catch (InterruptedException ignored) { }
    }

    @Override
    public Packet respond(Packet req) throws ConnectionException {
        if (req.getOrNull("gameId") == null)
            return null;
        try {
            GameController g = gameControllers.get(Integer.valueOf(req.getOrNull("gameId")));
            g.ridReq.get(Integer.valueOf(req.getOrNull("rid"))).listen(req);
        }
        catch (Exception e) {
            logger.info("Invalid server.request was sent to server.game server.controller - " + e.getMessage());
        }
        return null;
    }
}
