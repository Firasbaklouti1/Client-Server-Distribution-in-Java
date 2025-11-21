package game;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class PongGameState  implements Serializable {
private Ball ball;
private Paddle player1Paddle;
private Paddle player2Paddle;
private AtomicInteger score1, score2;
private ReentrantLock updateLock;
private final int padding=GameSetting.getPadding();
private final int mapLength =GameSetting.getMapLength();
private final int mapWidth=GameSetting.getMapWidth();

    public PongGameState() {
    }

    @Override
    public String toString() {
        return "PongGameState{" +
                "ball=" + ball +
                ", player1Paddle=" + player1Paddle +
                ", player2Paddle=" + player2Paddle +
                ", score1=" + score1 +
                ", score2=" + score2 +
                ", updateLock=" + updateLock +
                ", padding=" + padding +
                ", mapLength=" + mapLength +
                ", mapWidth=" + mapWidth +
                '}';
    }

    public void update(double deltaTime) {
        updateLock.lock();
        try {
            ball.update(deltaTime,mapLength,mapWidth);
            checkCollisions();
            checkScoring();
        }finally {
            updateLock.unlock();
            }
    }

    private void checkScoring() {
        if(ball.getX() >= mapLength ){
            score1.set(score1.get()+1);
            resetBall(1);
        }else if(ball.getX() <= 0){
            score2.set(score2.get()+1);
            resetBall(-1);
        }

    }

    private void resetBall(int direction) {
        ball = new Ball(mapLength,mapWidth,direction);
    }

    private void checkCollisions() {
        int ballRadius=GameSetting.getBallRadius();
        if (between(ball.getX()-ballRadius,padding-player1Paddle.getPaddleWidth(),padding) && Math.abs(ball.getY() - player1Paddle.getY()) <= player1Paddle.getPaddleHauteur() / 2) {
            System.out.println("collision avec player1Paddle");
            System.out.println("info su le ball before entring paddleCollision :"+ball);
            ball.paddleCollision(1, player1Paddle.getY());

        } else if (between(ball.getX()+ballRadius,mapLength - padding-player2Paddle.getPaddleWidth(),mapLength - padding+player2Paddle.getPaddleWidth())&& Math.abs(ball.getY() - player2Paddle.getY()) <= player2Paddle.getPaddleHauteur() / 2) {
            System.out.println("collision avec player2Paddle");
            System.out.println("info su le ball before entring paddleCollision :"+ball);
            ball.paddleCollision(2, player2Paddle.getY());

        } else if (ball.getY()-ballRadius <= 0 || ball.getY()+ballRadius >= mapWidth) {
            ball.wallCollision();
            System.out.println("collision avec wall");
        }
    }

    public void movePaddle(int playerId, int direction) {
            Paddle paddle=player1Paddle.getPlayerId()==playerId? player1Paddle:player2Paddle;
            paddle.movePaddle(direction, mapWidth);
    }

    public boolean between(int value,int inf,int sup){
        return (value>=inf && value<=sup);
    }


    public PongGameState(Ball ball, Paddle player1Paddle, Paddle player2Paddle, AtomicInteger score1, AtomicInteger score2, ReentrantLock updateLock) {
        this.ball = ball;
        this.player1Paddle = player1Paddle;
        this.player2Paddle = player2Paddle;
        this.score1 = score1;
        this.score2 = score2;
        this.updateLock = updateLock;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public Paddle getPlayer1Paddle() {
        return player1Paddle;
    }

    public void setPlayer1Paddle(Paddle player1Paddle) {
        this.player1Paddle = player1Paddle;
    }

    public Paddle getPlayer2Paddle() {
        return player2Paddle;
    }

    public void setPlayer2Paddle(Paddle player2Paddle) {
        this.player2Paddle = player2Paddle;
    }

    public AtomicInteger getScore1() {
        return score1;
    }

    public void setScore1(AtomicInteger score1) {
        this.score1 = score1;
    }

    public AtomicInteger getScore2() {
        return score2;
    }

    public void setScore2(AtomicInteger score2) {
        this.score2 = score2;
    }

    public ReentrantLock getUpdateLock() {
        return updateLock;
    }

    public void setUpdateLock(ReentrantLock updateLock) {
        this.updateLock = updateLock;
    }

    public int getPadding() {
        return padding;
    }

    public int getMapLength() {
        return mapLength;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void init() {
        int midY = GameSetting.getMapWidth() / 2;
        this.player1Paddle = new Paddle(1, midY);
        this.player2Paddle = new Paddle(2, midY);
        this.ball = new Ball(GameSetting.getMapLength(), GameSetting.getMapWidth());
        this.score1 = new AtomicInteger(0);
        this.score2 = new AtomicInteger(0);
        this.updateLock = new ReentrantLock();
    }
}