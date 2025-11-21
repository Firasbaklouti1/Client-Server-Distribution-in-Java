package game;

import java.io.Serializable;

public class Paddle implements Serializable {
    private int playerId;
    private int y;
    private final int paddleHauteur =GameSetting.getPaddleHauteur();
    private final int paddleWidth =GameSetting.getPaddleWidth();
    private final int movement = GameSetting.getMovement();

    public Paddle(int playerId, int y) {
        this.playerId = playerId;
        this.y = y;
    }


    public void movePaddle(int direction, int mapHeight) {
        if(direction==1 && (y - paddleHauteur / 2 - movement >= 0)) {
            y -= movement; // move up
            System.out.println("+");
        }
        else if(direction==0 && (y + paddleHauteur / 2 + movement <= mapHeight)) {
            y += movement; // move down
            System.out.println("-");
        }
    }








    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPaddleHauteur() {
        return paddleHauteur;
    }

    public int getPaddleWidth() {
        return paddleWidth;
    }

    public int getMovement() {
        return movement;
    }
}
