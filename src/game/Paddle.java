package game;

import java.io.Serializable;

/**
 * Represents a paddle in the Pong game.
 * Handles paddle movement and boundary checking.
 * Implements Serializable for network transmission.
 */
public class Paddle implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Direction constant for moving up */
    public static final int DIRECTION_UP = 1;
    /** Direction constant for moving down */
    public static final int DIRECTION_DOWN = 0;

    /** The player ID (1 for left paddle, 2 for right paddle) */
    private int playerId;
    /** The Y position of the paddle center */
    private int y;
    /** Height of the paddle (from settings) */
    private final int paddleHauteur = GameSetting.getPaddleHauteur();
    /** Width of the paddle (from settings) */
    private final int paddleWidth = GameSetting.getPaddleWidth();
    /** Movement amount per key press (from settings) */
    private final int movement = GameSetting.getMovement();

    /**
     * Creates a new paddle for a player.
     * @param playerId The player ID (1 or 2)
     * @param y Initial Y position (center of paddle)
     */
    public Paddle(int playerId, int y) {
        this.playerId = playerId;
        this.y = y;
    }

    /**
     * Moves the paddle in the specified direction, respecting map boundaries.
     * @param direction 1 for up, 0 for down
     * @param mapHeight The height of the game map
     */
    public void movePaddle(int direction, int mapHeight) {
        if (direction == DIRECTION_UP && canMoveUp()) {
            y -= movement;
        } else if (direction == DIRECTION_DOWN && canMoveDown(mapHeight)) {
            y += movement;
        }
    }

    /**
     * Checks if the paddle can move up without going out of bounds.
     * @return true if the paddle can move up
     */
    private boolean canMoveUp() {
        return y - paddleHauteur / 2 - movement >= 0;
    }

    /**
     * Checks if the paddle can move down without going out of bounds.
     * @param mapHeight The height of the game map
     * @return true if the paddle can move down
     */
    private boolean canMoveDown(int mapHeight) {
        return y + paddleHauteur / 2 + movement <= mapHeight;
    }

    // ==================== Getters and Setters ====================

    /** @return The player ID */
    public int getPlayerId() {
        return playerId;
    }

    /** @param playerId The new player ID */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /** @return The Y position of the paddle center */
    public int getY() {
        return y;
    }

    /** @param y The new Y position */
    public void setY(int y) {
        this.y = y;
    }

    /** @return The paddle height */
    public int getPaddleHauteur() {
        return paddleHauteur;
    }

    /** @return The paddle width */
    public int getPaddleWidth() {
        return paddleWidth;
    }

    /** @return The movement amount per key press */
    public int getMovement() {
        return movement;
    }
}
