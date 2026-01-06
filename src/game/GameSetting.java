package game;

/**
 * Configuration class containing all game settings and constants.
 * All settings are static and accessed through getter methods.
 * Modify the values below to adjust game parameters.
 */
public final class GameSetting {

    // Private constructor to prevent instantiation
    private GameSetting() {
        throw new UnsupportedOperationException("GameSetting is a utility class and cannot be instantiated");
    }

    /*
     * Alternative configuration for larger screens:
     * private static final int padding = 40;
     * private static final int mapLength = 1000;
     * private static final int mapWidth = 600;
     * private static final int paddleHauteur = 80;
     * private static final int paddleWidth = 20;
     * private static final int movement = 15;
     * private static final int PADDLE_SPEED = 10;
     * private static final int baseSpeed = 300;
     * private static final int ballRadius = 10;
     * private static final double augmentSpeedWithEveryPaddleCollision = 1.2;
     */

    // ==================== Map Settings ====================
    /** Padding from the edge of the screen to the paddles */
    private static final int padding = 20;
    /** Width of the game map (horizontal) */
    private static final int mapLength = 600;
    /** Height of the game map (vertical) */
    private static final int mapWidth = 400;

    // ==================== Paddle Settings ====================
    /** Height of the paddle */
    private static final int paddleHauteur = 40;
    /** Width of the paddle */
    private static final int paddleWidth = 10;
    /** Distance paddle moves per key press */
    private static final int movement = 7;
    /** Paddle movement speed (currently unused, consider removing or using) */
    private static final int PADDLE_SPEED = 5;

    // ==================== Ball Settings ====================
    /** Initial ball speed */
    private static final int baseSpeed = 150;
    /** Ball radius for collision detection */
    private static final int ballRadius = 5;
    /** Speed multiplier applied on each paddle collision */
    private static final double augmentSpeedWithEveryPaddleCollision = 1.1;

    // ==================== Getters ====================

    /**
     * @return Speed multiplier applied when ball hits a paddle
     */
    public static double getAugmentSpeedWithEveryPaddleCollision() {
        return augmentSpeedWithEveryPaddleCollision;
    }

    /**
     * @return Ball radius in pixels
     */
    public static int getBallRadius() {
        return ballRadius;
    }

    /**
     * @return Initial ball speed in pixels per second
     */
    public static int getBaseSpeed() {
        return baseSpeed;
    }

    /**
     * @return Paddle movement speed
     */
    public static int getPaddleSpeed() {
        return PADDLE_SPEED;
    }

    /**
     * @return Padding from screen edge to paddle position
     */
    public static int getPadding() {
        return padding;
    }

    /**
     * @return Width of the game map in pixels
     */
    public static int getMapLength() {
        return mapLength;
    }

    /**
     * @return Height of the game map in pixels
     */
    public static int getMapWidth() {
        return mapWidth;
    }

    /**
     * @return Height of the paddle in pixels
     */
    public static int getPaddleHauteur() {
        return paddleHauteur;
    }

    /**
     * @return Width of the paddle in pixels
     */
    public static int getPaddleWidth() {
        return paddleWidth;
    }

    /**
     * @return Distance paddle moves per key press in pixels
     */
    public static int getMovement() {
        return movement;
    }
}
