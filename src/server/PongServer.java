package server;

import game.PongGameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for the networked Pong game.
 * Handles two player connections, game state management, and network communication.
 * Runs the game loop and broadcasts state updates to all connected clients.
 */
public class PongServer {
    /** Server port to listen on */
    private static final int PORT = 12345;
    /** Target frame rate in milliseconds (16ms ≈ 60 FPS) */
    private static final int FRAME_RATE_MS = 16;
    /** Nanoseconds per second for delta time calculation */
    private static final double NS_PER_SECOND = 1_000_000_000.0;

    /** The game state */
    private PongGameState gameState;
    /** Array of player handlers (2 players) */
    private PlayerHandler[] players = new PlayerHandler[2];
    /** Flag indicating if the server is running */
    private volatile boolean running = true;

    /**
     * Creates a new Pong server and initializes the game state.
     */
    public PongServer() {
        gameState = new PongGameState();
        gameState.init();
    }

    /**
     * Starts the server and waits for player connections.
     * Once both players are connected, starts the game loop.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("========================================");
            System.out.println("   Pong Server started on port " + PORT);
            System.out.println("========================================");
            System.out.println("Waiting for players...\n");

            // Wait for Player 1
            System.out.print("Waiting for Player 1... ");
            Socket s1 = serverSocket.accept();
            System.out.println("Connected! (" + s1.getInetAddress().getHostAddress() + ")");
            players[0] = new PlayerHandler(s1, 1);
            players[0].start();

            // Wait for Player 2
            System.out.print("Waiting for Player 2... ");
            Socket s2 = serverSocket.accept();
            System.out.println("Connected! (" + s2.getInetAddress().getHostAddress() + ")");
            players[1] = new PlayerHandler(s2, 2);
            players[1].start();

            System.out.println("\n========================================");
            System.out.println("   Both players connected! Game starting!");
            System.out.println("========================================\n");

            gameLoop();

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    /**
     * Main game loop. Updates physics and broadcasts state at ~60 FPS.
     */
    private void gameLoop() {
        long lastTime = System.nanoTime();

        while (running && hasActivePlayers()) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / NS_PER_SECOND;
            lastTime = currentTime;

            // Update game physics
            gameState.update(deltaTime);

            // Send updated state to all players
            broadcastState();

            // Sleep to maintain ~60 FPS
            try {
                Thread.sleep(FRAME_RATE_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Game ended.");
    }

    /**
     * Checks if there are any active players connected.
     * @return true if at least one player is connected
     */
    private boolean hasActivePlayers() {
        for (PlayerHandler player : players) {
            if (player != null && player.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Broadcasts the current game state to all connected players.
     */
    private void broadcastState() {
        for (PlayerHandler player : players) {
            if (player != null && player.isConnected()) {
                player.sendState(gameState);
            }
        }
    }

    /**
     * Shuts down the server and closes all connections.
     */
    private void shutdown() {
        running = false;
        for (PlayerHandler player : players) {
            if (player != null) {
                player.disconnect();
            }
        }
        System.out.println("Server shutdown complete.");
    }

    /**
     * Inner class that handles communication with a single player.
     * Runs in its own thread to receive player input.
     */
    private class PlayerHandler extends Thread {
        private Socket socket;
        private int playerId;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private volatile boolean connected = true;

        /**
         * Creates a new handler for a player connection.
         * @param socket The player's socket connection
         * @param playerId The player's ID (1 or 2)
         */
        public PlayerHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
            this.setName("PlayerHandler-" + playerId);

            try {
                // Create output stream first to prevent deadlock
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Send player ID to client
                out.writeInt(playerId);
                out.flush();
            } catch (IOException e) {
                System.err.println("Failed to initialize Player " + playerId + ": " + e.getMessage());
                connected = false;
            }
        }

        /**
         * Sends the current game state to this player.
         * @param state The game state to send
         */
        public void sendState(PongGameState state) {
            if (!connected) return;

            try {
                out.reset(); // Clear object cache to send fresh state
                out.writeObject(state);
                out.flush();
            } catch (IOException e) {
                // Connection lost
                connected = false;
            }
        }

        /**
         * @return true if this player is still connected
         */
        public boolean isConnected() {
            return connected;
        }

        /**
         * Disconnects this player and closes resources.
         */
        public void disconnect() {
            connected = false;
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }

        @Override
        public void run() {
            try {
                while (connected && running) {
                    // Read movement command: 1 = Up, 0 = Down
                    int direction = in.readInt();
                    gameState.movePaddle(playerId, direction);
                }
            } catch (IOException e) {
                // Connection lost
            } finally {
                connected = false;
                System.out.println("Player " + playerId + " disconnected.");
            }
        }
    }

    /**
     * Main entry point for the Pong server.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        new PongServer().start();
    }
}