package game;

import java.io.Serializable;
import java.util.Random;

/**
 * Represents the ball in the Pong game.
 * Handles ball physics including movement, wall collisions, and paddle collisions.
 * Implements Serializable for network transmission.
 */
public class Ball implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Current X position of the ball */
    private int x;
    /** Current Y position of the ball */
    private int y;
    /** Horizontal velocity of the ball */
    private int vx;
    /** Vertical velocity of the ball */
    private int vy;

    /** Random number generator for initial ball direction */
    private static final Random RANDOM = new Random();

    /** Maximum initial vertical velocity */
    private static final int MAX_INITIAL_VY = 100;

    /**
     * Creates a ball with specified position and velocity.
     * @param x Initial X position
     * @param y Initial Y position
     * @param vx Initial horizontal velocity
     * @param vy Initial vertical velocity
     */
    public Ball(int x, int y, int vx, int vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Creates a ball at the center of the map with random initial direction.
     * @param mapLength The width of the game map
     * @param mapWidth The height of the game map
     */
    public Ball(int mapLength, int mapWidth) {
        int direction = RANDOM.nextBoolean() ? 1 : -1;
        this.x = mapLength / 2;
        this.y = mapWidth / 2;
        this.vx = direction * GameSetting.getBaseSpeed();
        // Random vertical velocity between -MAX_INITIAL_VY and +MAX_INITIAL_VY
        this.vy = RANDOM.nextInt(2 * MAX_INITIAL_VY + 1) - MAX_INITIAL_VY;
    }

    /**
     * Creates a ball at the center of the map with specified horizontal direction.
     * @param mapLength The width of the game map
     * @param mapWidth The height of the game map
     * @param direction The horizontal direction (-1 for left, 1 for right)
     */
    public Ball(int mapLength, int mapWidth, int direction) {
        this.x = mapLength / 2;
        this.y = mapWidth / 2;
        this.vx = direction * GameSetting.getBaseSpeed();
        // Random vertical velocity between -MAX_INITIAL_VY and +MAX_INITIAL_VY
        this.vy = RANDOM.nextInt(2 * MAX_INITIAL_VY + 1) - MAX_INITIAL_VY;
    }

    /**
     * Updates the ball position based on velocity and time elapsed.
     * Keeps the ball within the map boundaries.
     * @param deltaTime Time elapsed since last update in seconds
     * @param mapLength The width of the game map
     * @param mapWidth The height of the game map
     */
    public void update(double deltaTime, int mapLength, int mapWidth) {
        this.x = keepInside(x + (int) (vx * deltaTime), 0, mapLength);
        this.y = keepInside(y + (int) (vy * deltaTime), 0, mapWidth);
    }

    /**
     * Handles collision with top or bottom walls by reversing vertical velocity.
     */
    public void wallCollision() {
        this.vy *= -1;
    }

    /**
     * Handles collision with a paddle.
     * Increases ball speed slightly and calculates new trajectory based on hit position.
     * @param paddleId The ID of the paddle (1 for left, 2 for right)
     * @param paddleY The Y position of the paddle center
     */
    public void paddleCollision(int paddleId, int paddleY) {
        // Apply speed multiplier for paddle collisions
        double speedMultiplier = GameSetting.getAugmentSpeedWithEveryPaddleCollision();

        // Calculate relative hit position (-1 to 1) for angle calculation
        double relativeIntersectY = (paddleY - this.y) / (GameSetting.getPaddleHauteur() / 2.0);

        // Clamp to valid range to prevent extreme angles
        relativeIntersectY = Math.max(-1.0, Math.min(1.0, relativeIntersectY));

        // Max bounce angle is 45 degrees (PI/4 radians)
        double bounceAngle = relativeIntersectY * Math.PI / 4;

        // Calculate new speed with multiplier
        double speed = Math.sqrt(vx * vx + vy * vy) * speedMultiplier;

        // Set new velocity based on angle (direction depends on which paddle was hit)
        this.vx = (int) (speed * Math.cos(bounceAngle)) * (paddleId == 1 ? 1 : -1);
        this.vy = (int) (-speed * Math.sin(bounceAngle));
    }

    /**
     * Utility method to constrain a value within a specified range.
     * @param value The value to constrain
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The constrained value
     */
    public static int keepInside(int value, int min, int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }


    // ==================== Getters and Setters ====================

    /** @return Current X position of the ball */
    public int getX() {
        return x;
    }

    /** @param x New X position */
    public void setX(int x) {
        this.x = x;
    }

    /** @return Current Y position of the ball */
    public int getY() {
        return y;
    }

    /** @param y New Y position */
    public void setY(int y) {
        this.y = y;
    }

    /** @return Current horizontal velocity */
    public int getVx() {
        return vx;
    }

    /** @param vx New horizontal velocity */
    public void setVx(int vx) {
        this.vx = vx;
    }

    /** @return Current vertical velocity */
    public int getVy() {
        return vy;
    }

    /** @param vy New vertical velocity */
    public void setVy(int vy) {
        this.vy = vy;
    }

    @Override
    public String toString() {
        return "Ball{" +
                "x=" + x +
                ", y=" + y +
                ", vx=" + vx +
                ", vy=" + vy +
                '}';
    }
}
