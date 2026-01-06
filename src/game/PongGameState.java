package game;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents the complete state of a Pong game.
 * Contains all game objects (ball, paddles) and scores.
 * Thread-safe for use in networked multiplayer.
 * Implements Serializable for network transmission.
 */
public class PongGameState implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The game ball */
    private Ball ball;
    /** Left paddle (Player 1) */
    private Paddle player1Paddle;
    /** Right paddle (Player 2) */
    private Paddle player2Paddle;
    /** Score for Player 1 */
    private AtomicInteger score1;
    /** Score for Player 2 */
    private AtomicInteger score2;
    /** Lock for thread-safe updates */
    private transient ReentrantLock updateLock;

    // Game map dimensions (from settings)
    private final int padding = GameSetting.getPadding();
    private final int mapLength = GameSetting.getMapLength();
    private final int mapWidth = GameSetting.getMapWidth();

    /**
     * Default constructor. Call init() to initialize game objects.
     */
    public PongGameState() {
    }

    /**
     * Initializes all game objects with default values.
     * Sets paddles at center height, ball at center with random direction.
     */
    public void init() {
        int midY = GameSetting.getMapWidth() / 2;
        this.player1Paddle = new Paddle(1, midY);
        this.player2Paddle = new Paddle(2, midY);
        this.ball = new Ball(GameSetting.getMapLength(), GameSetting.getMapWidth());
        this.score1 = new AtomicInteger(0);
        this.score2 = new AtomicInteger(0);
        this.updateLock = new ReentrantLock();
    }

    @Override
    public String toString() {
        return "PongGameState{" +
                "ball=" + ball +
                ", player1Paddle=" + player1Paddle +
                ", player2Paddle=" + player2Paddle +
                ", score1=" + score1 +
                ", score2=" + score2 +
                ", mapLength=" + mapLength +
                ", mapWidth=" + mapWidth +
                '}';
    }

    /**
     * Updates the game state for one frame.
     * Moves the ball, checks collisions, and updates scores.
     * Thread-safe using ReentrantLock.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (updateLock == null) {
            updateLock = new ReentrantLock();
        }
        updateLock.lock();
        try {
            ball.update(deltaTime, mapLength, mapWidth);
            checkCollisions();
            checkScoring();
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Checks if a player has scored and updates the score.
     * Resets the ball after each point.
     */
    private void checkScoring() {
        if (ball.getX() >= mapLength) {
            // Ball passed right edge - Player 1 scores
            score1.incrementAndGet();
            resetBall(1); // Ball goes toward Player 2
        } else if (ball.getX() <= 0) {
            // Ball passed left edge - Player 2 scores
            score2.incrementAndGet();
            resetBall(-1); // Ball goes toward Player 1
        }
    }

    /**
     * Resets the ball to the center with the specified direction.
     * @param direction -1 for left, 1 for right
     */
    private void resetBall(int direction) {
        ball = new Ball(mapLength, mapWidth, direction);
    }

    /**
     * Checks for collisions between the ball and paddles/walls.
     * Handles bounce physics for each collision type.
     */
    private void checkCollisions() {
        int ballRadius = GameSetting.getBallRadius();

        // Check collision with left paddle (Player 1)
        boolean inLeftPaddleXRange = between(
                ball.getX() - ballRadius,
                padding - player1Paddle.getPaddleWidth(),
                padding + player1Paddle.getPaddleWidth()
        );
        boolean hitsLeftPaddle = Math.abs(ball.getY() - player1Paddle.getY()) <= player1Paddle.getPaddleHauteur() / 2;

        // Check collision with right paddle (Player 2)
        int rightPaddleX = mapLength - padding;
        boolean inRightPaddleXRange = between(
                ball.getX() + ballRadius,
                rightPaddleX - player2Paddle.getPaddleWidth(),
                rightPaddleX + player2Paddle.getPaddleWidth()
        );
        boolean hitsRightPaddle = Math.abs(ball.getY() - player2Paddle.getY()) <= player2Paddle.getPaddleHauteur() / 2;

        if (inLeftPaddleXRange && hitsLeftPaddle) {
            ball.paddleCollision(1, player1Paddle.getY());
        } else if (inRightPaddleXRange && hitsRightPaddle) {
            ball.paddleCollision(2, player2Paddle.getY());
        } else if (ball.getY() - ballRadius <= 0 || ball.getY() + ballRadius >= mapWidth) {
            // Wall collision (top or bottom)
            ball.wallCollision();
        }
    }

    /**
     * Moves the specified player's paddle in the given direction.
     * @param playerId The player (1 or 2)
     * @param direction 1 for up, 0 for down
     */
    public void movePaddle(int playerId, int direction) {
        Paddle paddle = (player1Paddle.getPlayerId() == playerId) ? player1Paddle : player2Paddle;
        paddle.movePaddle(direction, mapWidth);
    }

    /**
     * Checks if a value is within a specified range (inclusive).
     * @param value The value to check
     * @param inf The lower bound
     * @param sup The upper bound
     * @return true if inf <= value <= sup
     */
    public boolean between(int value, int inf, int sup) {
        return value >= inf && value <= sup;
    }

    /**
     * Full constructor for creating a game state with all objects.
     */
    public PongGameState(Ball ball, Paddle player1Paddle, Paddle player2Paddle,
                         AtomicInteger score1, AtomicInteger score2, ReentrantLock updateLock) {
        this.ball = ball;
        this.player1Paddle = player1Paddle;
        this.player2Paddle = player2Paddle;
        this.score1 = score1;
        this.score2 = score2;
        this.updateLock = updateLock;
    }

    // ==================== Getters and Setters ====================

    /** @return The game ball */
    public Ball getBall() {
        return ball;
    }

    /** @param ball The new ball */
    public void setBall(Ball ball) {
        this.ball = ball;
    }

    /** @return Player 1's paddle */
    public Paddle getPlayer1Paddle() {
        return player1Paddle;
    }

    /** @param player1Paddle The new paddle for Player 1 */
    public void setPlayer1Paddle(Paddle player1Paddle) {
        this.player1Paddle = player1Paddle;
    }

    /** @return Player 2's paddle */
    public Paddle getPlayer2Paddle() {
        return player2Paddle;
    }

    /** @param player2Paddle The new paddle for Player 2 */
    public void setPlayer2Paddle(Paddle player2Paddle) {
        this.player2Paddle = player2Paddle;
    }

    /** @return Player 1's score */
    public AtomicInteger getScore1() {
        return score1;
    }

    /** @param score1 The new score for Player 1 */
    public void setScore1(AtomicInteger score1) {
        this.score1 = score1;
    }

    /** @return Player 2's score */
    public AtomicInteger getScore2() {
        return score2;
    }

    /** @param score2 The new score for Player 2 */
    public void setScore2(AtomicInteger score2) {
        this.score2 = score2;
    }

    /** @return The update lock */
    public ReentrantLock getUpdateLock() {
        return updateLock;
    }

    /** @param updateLock The new update lock */
    public void setUpdateLock(ReentrantLock updateLock) {
        this.updateLock = updateLock;
    }

    /** @return The padding value */
    public int getPadding() {
        return padding;
    }

    /** @return The map width (horizontal size) */
    public int getMapLength() {
        return mapLength;
    }

    /** @return The map height (vertical size) */
    public int getMapWidth() {
        return mapWidth;
    }
}