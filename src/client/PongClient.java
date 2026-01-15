package client;

import game.GameSetting;
import game.Paddle;
import game.PongGameState;
import game.PongViewer;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client application for the networked Pong game.
 * Connects to a PongServer and handles local input and rendering.
 * Sends paddle movement commands to server and receives game state updates.
 */
public class PongClient {
    /** Server address to connect to */
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    /** Server port */
    private static final int DEFAULT_PORT = 12345;

    /** Socket connection to the server */
    private Socket socket;
    /** Output stream for sending commands to server */
    private ObjectOutputStream out;
    /** Input stream for receiving game state from server */
    private ObjectInputStream in;
    /** The game viewer panel */
    private PongViewer viewer;
    /** The local player's ID (1 or 2) */
    private int myPlayerId;
    /** Flag indicating if the client is running */
    private volatile boolean running = true;

    /**
     * Creates and starts a new Pong client.
     * Connects to the server, receives player ID, and starts listening for updates.
     */
    public PongClient() {
        this(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
    }

    /**
     * Creates and starts a new Pong client with custom server settings.
     * @param serverAddress The server address
     * @param port The server port
     */
    public PongClient(String serverAddress, int port) {
        try {
            // Connect to server
            System.out.println("Connecting to server at " + serverAddress + ":" + port + "...");
            socket = new Socket(serverAddress, port);

            // Create streams (output before input to prevent deadlock)
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Receive player ID from server
            myPlayerId = in.readInt();
            System.out.println("Connected as Player " + myPlayerId);

            // Initialize GUI and start listening for game updates
            setupGUI();
            startListening();

        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            showErrorDialog("Connection Failed",
                    "Could not connect to server at " + serverAddress + ":" + port);
        }
    }

    /**
     * Sets up the game window and keyboard controls.
     */
    private void setupGUI() {
        JFrame frame = new JFrame("Pong - Player " + myPlayerId);
        viewer = new PongViewer(myPlayerId);

        // Add keyboard listener for paddle control
        viewer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        frame.setContentPane(viewer);
        frame.setSize(GameSetting.getMapLength(), GameSetting.getMapWidth() + 40); // +40 for title bar
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);

        // Request focus for keyboard input
        viewer.requestFocusInWindow();
    }

    /**
     * Handles keyboard input and sends commands to server.
     * @param e The key event
     */
    private void handleKeyPress(KeyEvent e) {
        try {
            int keyCode = e.getKeyCode();

            // Send movement command based on key pressed
            // UP: Arrow Up, A, or P
            // DOWN: Arrow Down, Q, or M
            if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_A ) {
                sendCommand(Paddle.DIRECTION_UP);
            } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_Q ) {
                sendCommand(Paddle.DIRECTION_DOWN);
            }
        } catch (IOException ex) {
            System.err.println("Failed to send command: " + ex.getMessage());
        }
    }

    /**
     * Sends a movement command to the server.
     * @param direction The direction (1 for up, 0 for down)
     * @throws IOException if sending fails
     */
    private synchronized void sendCommand(int direction) throws IOException {
        out.writeInt(direction);
        out.flush();
    }

    /**
     * Starts the loop that listens for game state updates from the server.
     * Runs until the connection is lost.
     */
    private void startListening() {
        try {
            while (running) {
                // Receive and apply game state from server
                PongGameState state = (PongGameState) in.readObject();
                viewer.updateGame(state);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection to server lost.");
        } finally {
            cleanup();
        }
    }

    /**
     * Cleans up resources when the client is shutting down.
     */
    private void cleanup() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignore cleanup errors
        }
        System.exit(0);
    }

    /**
     * Shows an error dialog to the user.
     * @param title Dialog title
     * @param message Error message
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * Main entry point for the Pong client.
     * @param args Optional: server address and port (e.g., "192.168.1.100 12345")
     */
    public static void main(String[] args) {
        if (args.length >= 2) {
            String address = args[0];
            int port = Integer.parseInt(args[1]);
            new PongClient(address, port);
        } else if (args.length == 1) {
            new PongClient(args[0], DEFAULT_PORT);
        } else {
            new PongClient();
        }
    }
}
