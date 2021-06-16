package server.game;

import server.controller.Controller;
import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.exception.ValidationException;
import server.db.model.GameState;
import server.middleware.ServerRID;
import server.util.RidUtilities;
import shared.handler.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.lock.CustomLock;
import shared.request.Packet;
import shared.request.PacketListener;
import shared.request.StatusCode;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GameController extends Controller {
    private static final int SET_BOARD_TTL = 30000, REFRESH_BOARD_TTL = 10000;
    private static final Logger logger = LogManager.getLogger(GameController.class);
    private static final ConcurrentHashMap<Integer, GameController> gameControllers = new ConcurrentHashMap<>();
    private final GameState gameState;
    private final Player[] players = new Player[2];
    private final ArrayList<SocketHandler> visitors = new ArrayList<>();
    Context context = new Context();

    public GameController(Player player1, Player player2) {
        System.out.println("New game is getting created!!!! - users: " + player1.getId() + ' ' + player2.getId());
        this.gameState = new GameState();
        try {
            context.gameStates.save(gameState);
        } catch (ConnectionException e) {
            logger.error("couldn't connect to database");
        } catch (ValidationException e) {
            logger.fatal("error while creating an empty server.game state - " + e.getLog());
        }
        Packet packet = new Packet("new-game");
        packet.addObject("game-id", gameState.id);
        packet.put("player-id", 0);
        player1.sendPacket(packet);
        packet.put("player-id", 1);
        player2.sendPacket(packet);
        gameControllers.put(gameState.id, this);
        players[0] = player1;
        players[1] = player2;
        new Thread(() -> setBoards(player1)).start();
        new Thread(() -> setBoards(player2)).start();
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
            builder.clear();
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
            LocalTime localTime = LocalTime.now();
            localTime = localTime.plusNanos(time.get() * 1000000L);
            packet.addObject("timeout", localTime);

            Packet response = null;
            try {
                System.out.println("new board was sent to player " + player.getId());
                logger.info("new board was sent to player " + player.getId());
                // player timeout
                if (time.get() <= 0)
                    break;
                // DEBUG
                builder.printBoard();
                response = RidUtilities.sendPacketAndGetResponse(packet, player.getSocketHandler());
            } catch (InterruptedException e) {
                System.out.println("automatically set board player timeout");
                logger.info("skipped user response while setting board due timeout");
                break;
            }
            assert response != null;
            if (i == 2) {
                System.out.println("player max rejects reached " + player.getPlayerNumber());
                logger.info("player max rejects reached - " + player.getPlayerNumber());
                break;
            }
            if (response.getOrNull("accept").equals("true")) {
                System.out.println("player " + player.getId() + " accepted the board");
                logger.info("player " + player.getId() + " accepted the board");
                break;
            }
            System.out.println("player " + player.getId() + " rejected the board");
            logger.info("player " + player.getId() + " rejected the board");
            time.addAndGet(REFRESH_BOARD_TTL);
        }
        timer.purge();
        timer.cancel();
        gameState.setBoard(player.getPlayerNumber(), builder.getBoard());
        player.setReady(true);
        System.out.println("final board was sent to player " + player.getPlayerNumber());
        Packet packet = new Packet("set-board");
        packet.addObject("board", builder.getBoard());
        player.getSocketHandler().sendPacket(packet);
    }

    @Override
    public Packet respond(Packet req) throws ConnectionException {
        if (req.getOrNull("game-id") == null)
            return new Packet(StatusCode.NOT_FOUND);
        try {
            GameController g = gameControllers.get(Integer.valueOf(req.getOrNull("game-id")));
            // TO DO answer users requests
        }
        catch (Exception e) {
            logger.info("Invalid request was sent to game controller - " + e.getMessage());
        }
        return new Packet(StatusCode.NOT_FOUND);
    }
}
