package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Graphics panel for rendering the Pong game for a single player.
 * Handles keyboard input and visual rendering.
 * Used when a player wants to control only their own paddle.
 */
public class GameGraphic extends JPanel {
    /** Reference to the game state */
    private PongGameState game;
    /** The ID of the player using this panel */
    private int playerID;

    /** Timer for refreshing the display at ~60 FPS */
    private Timer refreshTimer;
    /** Frame rate in milliseconds (16ms ≈ 60 FPS) */
    private static final int FRAME_RATE_MS = 16;

    /** Ball display size in pixels */
    private static final int BALL_DISPLAY_SIZE = 20;

    /**
     * Creates a new game graphics panel for a specific player.
     * @param game The game state to render
     * @param playerID The ID of the player (1 or 2)
     */
    public GameGraphic(PongGameState game, int playerID) {
        this.game = game;
        this.playerID = playerID;

        // Start timer for 60 FPS refresh
        refreshTimer = new Timer(FRAME_RATE_MS, e -> repaint());
        refreshTimer.start();

        // Enable keyboard focus
        setFocusable(true);
        requestFocusInWindow();

        // Add key listener for paddle controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    /**
     * Handles keyboard input for paddle movement.
     * @param e The key event
     */
    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_UP:
                // Move paddle up
                game.movePaddle(playerID, Paddle.DIRECTION_UP);
                break;
            case KeyEvent.VK_Q:
            case KeyEvent.VK_DOWN:
                // Move paddle down
                game.movePaddle(playerID, Paddle.DIRECTION_DOWN);
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
        g.drawString("Controls: A/Q (Up/Down)", GameSetting.getMapLength() / 2 - 60, 20);
    }

    /**
     * Stops the refresh timer. Should be called when the panel is no longer needed.
     */
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}
