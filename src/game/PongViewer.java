package game;

import javax.swing.*;
import java.awt.*;

/**
 * Graphics panel for rendering the Pong game in network mode.
 * Receives game state updates from the server and renders them.
 * KeyListener is added externally by PongClient.
 */
public class PongViewer extends JPanel {
    /** Current game state to render */
    private volatile PongGameState game;
    /** The ID of the local player */
    private int myPlayerId;

    /** Ball display size in pixels */
    private static final int BALL_DISPLAY_SIZE = 20;
    /** Font for score display */
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 18);
    /** Padding for info text from bottom */
    private static final int INFO_TEXT_Y = 380;

    /**
     * Creates a new viewer for a network client.
     * @param myPlayerId The ID of the local player (1 or 2)
     */
    public PongViewer(int myPlayerId) {
        this.myPlayerId = myPlayerId;
        this.game = new PongGameState(); // Empty state initially

        setFocusable(true);
        requestFocusInWindow();

        // KeyListener will be added by PongClient for network input handling
    }

    /**
     * Updates the displayed game state.
     * Called when new state is received from server.
     * @param newGame The new game state
     */
    public void updateGame(PongGameState newGame) {
        this.game = newGame;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Don't render if game state is not initialized
        if (game == null || game.getBall() == null) {
            drawWaitingScreen(g);
            return;
        }

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
        g.setFont(SCORE_FONT);
        g.drawString("Player 1: " + game.getScore1().get(), 50, 30);
        g.drawString("Player 2: " + game.getScore2().get(), GameSetting.getMapLength() - 150, 30);

        // Draw player info
        g.setColor(Color.YELLOW);
        g.drawString("You are Player " + myPlayerId, GameSetting.getMapLength() / 2 - 70, INFO_TEXT_Y);

        // Draw control hint
        g.setColor(Color.GRAY);
        g.setFont(g.getFont().deriveFont(12f));
        g.drawString("Controls: UP/DOWN or A/Q", GameSetting.getMapLength() / 2 - 70, INFO_TEXT_Y + 20);
    }

    /**
     * Draws a waiting screen when game state is not yet available.
     * @param g The graphics context
     */
    private void drawWaitingScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        g.drawString("Waiting for game to start...", getWidth() / 2 - 100, getHeight() / 2);
    }

    /** @return The local player's ID */
    public int getMyPlayerId() {
        return myPlayerId;
    }
}