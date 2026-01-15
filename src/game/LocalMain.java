package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main panel and entry point for local two-player Pong game.
 * Both players play on the same keyboard.
 * Player 1 uses A/Q keys, Player 2 uses P/M keys.
 */
public class LocalMain extends JPanel {
    /** Reference to the game state */
    private PongGameState game;

    /** Timer for refreshing display at ~60 FPS */
    private Timer refreshTimer;
    /** Frame rate in milliseconds (16ms ≈ 60 FPS) */
    private static final int FRAME_RATE_MS = 16;

    /** Ball display size in pixels */
    private static final int BALL_DISPLAY_SIZE = 20;

    /**
     * Creates a new local game panel.
     * @param game The game state to render and control
     */
    public LocalMain(PongGameState game) {
        this.game = game;

        // Start timer for 60 FPS refresh
        refreshTimer = new Timer(FRAME_RATE_MS, e -> repaint());
        refreshTimer.start();

        // Enable keyboard focus
        setFocusable(true);
        requestFocusInWindow();

        // Add key listener for both players
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    /**
     * Handles keyboard input for both players.
     * Player 1: A (up), Q (down)
     * Player 2: P (up), M (down)
     * @param e The key event
     */
    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            // Player 1 controls
            case KeyEvent.VK_A:
                game.movePaddle(1, Paddle.DIRECTION_UP);
                break;
            case KeyEvent.VK_Q:
                game.movePaddle(1, Paddle.DIRECTION_DOWN);
                break;
            // Player 2 controls
            case KeyEvent.VK_UP:
                game.movePaddle(2, Paddle.DIRECTION_UP);
                break;
            case KeyEvent.VK_DOWN:
                game.movePaddle(2, Paddle.DIRECTION_DOWN);
                break;
        }
        repaint(); // Refresh immediately after paddle movement
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw game objects in white
        g.setColor(Color.WHITE);

        // Draw ball
        g.fillOval(
                game.getBall().getX(),
                game.getBall().getY(),
                BALL_DISPLAY_SIZE, BALL_DISPLAY_SIZE
        );

        // Draw left paddle (Player 1)
        g.fillRect(
                GameSetting.getPadding(),
                game.getPlayer1Paddle().getY() - GameSetting.getPaddleHauteur() / 2,
                GameSetting.getPaddleWidth(),
                GameSetting.getPaddleHauteur()
        );

        // Draw right paddle (Player 2)
        g.fillRect(
                GameSetting.getMapLength() - GameSetting.getPadding() - GameSetting.getPaddleWidth(),
                game.getPlayer2Paddle().getY() - GameSetting.getPaddleHauteur() / 2,
                GameSetting.getPaddleWidth(),
                GameSetting.getPaddleHauteur()
        );

        // Draw scores
        g.drawString("Player 1: " + game.getScore1().get(), 50, 20);
        g.drawString("Player 2: " + game.getScore2().get(), GameSetting.getMapLength() - 100, 20);

        // Draw controls reminder
        g.drawString("Controls: A/Q (P1)  P/M (P2)", GameSetting.getMapLength() / 2 - 80, 20);
    }

    /**
     * Main entry point for local two-player game.
     * Initializes game state and starts the game loop.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize game state
        PongGameState game = new PongGameState();
        game.init();

        // Create and configure the game window
        JFrame frame = new JFrame("Pong Game - Controls: A/Q (P1) | P/M (P2)");
        LocalMain viewer = new LocalMain(game);
        frame.setContentPane(viewer);
        frame.setSize(GameSetting.getMapLength(), GameSetting.getMapWidth() + 40); // +40 for title bar
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);

        // Game loop with delta time calculation
        long lastTime = System.nanoTime();
        final double NS_PER_SECOND = 1_000_000_000.0;

        while (true) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / NS_PER_SECOND;
            lastTime = currentTime;

            game.update(deltaTime);

            try {
                Thread.sleep(FRAME_RATE_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}