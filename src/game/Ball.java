package game;

import java.io.Serializable;
import java.util.Random;

public class Ball implements Serializable {
    private int x;
    private int y;
    private int vx;
    private int vy;

    public Ball(int x, int y, int vx, int vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public Ball(int mapLength, int mapWidth) {
        Random random = new Random();
        int direct=random.nextInt(2);
        direct=direct==0?1:-1;
        this.x=mapLength/2;
        this.y=mapWidth/2;
        this.vx = direct*GameSetting.getBaseSpeed();
        this.vy = random.nextInt(-100,200);
    }

    public Ball(int mapLength, int mapWidth, int direction) {
        Random random = new Random();
        ;
        this.x=mapLength/2;
        this.y=mapWidth/2;
        this.vx = direction*GameSetting.getBaseSpeed();
        this.vy = random.nextInt(-100,200);
    }

    public void update(double deltaTime,int mapLength,int mapWidth) {
        this.x = keepInside(x + (int) (vx * deltaTime), 0, mapLength);
        this.y = keepInside(y + (int) (vy * deltaTime), 0, mapWidth);
    }

    public void wallCollision(){
        this.vy*=-1;
    }

    public void paddleCollision(int paddleId, int paddleY) {
        System.out.println("entre dans la fonction paddleCollision");
        this.vx = (int) (GameSetting.getAugmentSpeedWithEveryPaddleCollision()*this.vx); // Slight speed increase on bounce

        // Calculate relative hit position (-1 to 1)
        double relativeIntersectY = (paddleY - this.y) / (GameSetting.getPaddleHauteur() / 2.0);
        double bounceAngle = relativeIntersectY * Math.PI/4; // Max 45 degree angle

        // Set new velocity based on angle
        double speed = Math.sqrt(vx*vx + vy*vy);
        this.vx = (int)(speed * Math.cos(bounceAngle)) * (paddleId == 1 ? 1 : -1);
        this.vy = (int)(-speed * Math.sin(bounceAngle));
    }

    public static int keepInside(int value, int min, int max) {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        else
            return value;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public int getVy() {
        return vy;
    }

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
