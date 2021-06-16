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

import java.util.ArrayList;
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
        packet.addObject("game-id", gameState.id);
        player1.sendPacket(packet);
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
                response = RidUtilities.sendPacketAndGetResponse(packet, player.getSocketHandler());
            } catch (InterruptedException e) {
                gameState.setBoard(player.getId(), builder.getBoard());
                player.setReady(true);
                logger.info("skipped user response while setting board due timeout");
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
