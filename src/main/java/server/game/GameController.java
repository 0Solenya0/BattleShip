package server.game;

import server.controller.Controller;
import server.db.Context;
import server.db.exception.ConnectionException;
import server.db.exception.ValidationException;
import server.db.model.GameState;
import server.util.RidUtilities;
import shared.handler.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.model.Board;
import shared.request.Packet;
import shared.request.StatusCode;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController extends Controller {
    private static final int SET_BOARD_TTL = 30000, REFRESH_BOARD_TTL = 10000, TURN_TTL = 25000;
    private static final Logger logger = LogManager.getLogger(GameController.class);
    private static final ConcurrentHashMap<Integer, GameController> gameControllers = new ConcurrentHashMap<>();
    private final GameState gameState;
    private final Player[] players = new Player[2];
    private final ArrayList<SocketHandler> visitors = new ArrayList<>(); // TO DO make observer class
    private final Context context = new Context();
    private Timer turnTimer = new Timer();
    private LocalTime turnStart;

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
        packet.put("username-p1", player1.getUser().getUsername());
        packet.put("username-p2", player2.getUser().getUsername());
        packet.put("player-id", 0);
        player1.sendPacket(packet);
        packet.put("player-id", 1);
        player2.sendPacket(packet);
        gameControllers.put(gameState.id, this);
        players[0] = player1;
        players[1] = player2;
        player1.getReady().addObserver(this::startGame);
        player2.getReady().addObserver(this::startGame);
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
        System.out.println("final board was sent to player " + player.getPlayerNumber());
        Packet packet = new Packet("set-board");
        packet.addObject("board", builder.getBoard());
        player.getSocketHandler().sendPacket(packet);
        player.setReady(true);
        // TO DO save the game state
    }

    public Board getGameBoardByUser(int playerNumber, int userId) {
        Board board = new Board();
        board.copy(gameState.getBoard(playerNumber));
        if (players[playerNumber].getId() != userId)
            board.hideShips();
        return board;
    }

    public void sendGameStateToUser(int userId, SocketHandler socketHandler) {
        Packet packet = new Packet("game-data");
        packet.addObject("board-p1", getGameBoardByUser(0, userId));
        packet.addObject("board-p2", getGameBoardByUser(1, userId));
        packet.put("turn", gameState.getTurn());
        packet.addObject("timeout", turnStart.plusNanos(TURN_TTL * 1000000L));
        socketHandler.sendPacket(packet);
    }

    public void sendEndTurnToAll() {
        Packet packet = new Packet("end-turn");
        players[0].getSocketHandler().sendPacket(packet);
        players[1].getSocketHandler().sendPacket(packet);
    }

    public void startGame(Boolean tmp) {
        if (!players[0].getReady().get() || !players[1].getReady().get())
            return;
        startNewTurn();
    }

    public synchronized void startNewTurn() {
        if (gameState.getTurn() != -1)
            sendEndTurnToAll();
        gameState.nextTurn();
        turnStart = LocalTime.now();
        sendGameStateToUser(players[0].getId(), players[0].getSocketHandler());
        sendGameStateToUser(players[1].getId(), players[1].getSocketHandler());
        turnTimer.cancel();
        turnTimer.purge();
        turnTimer = new Timer();
        // TO DO save game state
        int curTurn = gameState.getTurn();
        turnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (gameState.getTurn() == curTurn)
                    startNewTurn();
            }
        }, TURN_TTL);
    }

    public void playTurn(Packet req, Player player) {
        if (gameState.getTurn() != req.getInt("turn")
                || req.getInt("turn") % 2 != player.getPlayerNumber())
            return;
        System.out.println("player played turn - " + player.getPlayerNumber());
        int row = req.getInt("row"), col = req.getInt("col");
        gameState.getBoard(1 - player.getPlayerNumber()).bomb(row, col);
        // TO DO save game state
        startNewTurn();
    }

    public void answerObserver(Packet packet) {
        // TO DO
    }

    public static Packet respond(Packet req) throws ConnectionException {
        if (req.getOrNull("game-id") == null)
            return new Packet(StatusCode.NOT_FOUND);
        try {
            GameController g = gameControllers.get(req.getInt("game-id"));
            if (req.target.equals("game-play-turn")) {
                if (req.getInt("handler") == g.players[0].getSocketHandler().getId())
                    g.playTurn(req, g.players[0]);
                else if (req.getInt("handler") == g.players[1].getSocketHandler().getId())
                    g.playTurn(req, g.players[1]);
            }
        }
        catch (Exception e) {
            logger.info("Invalid request was sent to game controller - " + e.getMessage());
        }
        return new Packet(StatusCode.NOT_FOUND);
    }
}
