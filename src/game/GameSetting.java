package game;

public class GameSetting {
    /*
    * private static final int padding = 40;
    private static final int mapLength = 1000;
    private static final int mapWidth = 600;
    private static final int paddleHauteur = 80;
    private static final int paddleWidth = 20;
    private static final int movement = 15;
    private static final int PADDLE_SPEED = 10;
    private static final int baseSpeed = 300;
    private static final int ballRadius = 10;
    private static final double augmentSpeedWithEveryPaddleCollision=1.2;*/

    private static final int padding = 20;
    private static final int mapLength = 600;
    private static final int mapWidth = 400;
    private static final int paddleHauteur = 40;
    private static final int paddleWidth = 10;
    private static final int movement = 7;
    private static final int PADDLE_SPEED = 5;
    private static final int baseSpeed = 150;
    private static final int ballRadius = 5;
    private static final double augmentSpeedWithEveryPaddleCollision=1.1;


    public static double getAugmentSpeedWithEveryPaddleCollision(){
        return augmentSpeedWithEveryPaddleCollision;
    }


    public static int getBallRadius(){
        return ballRadius;
    }

    public static int getBaseSpeed(){
        return baseSpeed;
    }
    public static int getPaddleSpeed(){
        return PADDLE_SPEED;
    }


    public static int getPadding() {
        return padding;
    }

    public static int getMapLength() {
        return mapLength;
    }

    public static int getMapWidth() {
        return mapWidth;
    }

    public static int getPaddleHauteur() {
        return paddleHauteur;
    }

    public static int getPaddleWidth() {
        return paddleWidth;
    }

    public static int getMovement() {
        return movement;
    }
}
