package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class LocalMain extends JPanel {

    private PongGameState game;

    public LocalMain(PongGameState game) {
        this.game = game;
        new Timer(16, e -> repaint()).start(); // repaint 60 fps

        // Enable keyboard focus
        setFocusable(true);
        requestFocusInWindow();

        // Add key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_A:
                // Player 1 up
                game.movePaddle(1, 1);
                System.out.println("Player 1 up");
                break;
            case KeyEvent.VK_Q:
                // Player 1 down
                game.movePaddle(1, 0);
                System.out.println("Player 1 down");
                break;
            case KeyEvent.VK_P:
                // Player 2 up
                game.movePaddle(2, 1);
                System.out.println("Player 2 up");
                break;
            case KeyEvent.VK_M:
                // Player 2 down
                game.movePaddle(2, 0);
                System.out.println("Player 2 down");
                break;
        }
        repaint(); // Refresh the display immediately when paddles move
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Set white for game objects
        g.setColor(Color.WHITE);

        // Ball
        g.fillOval(
                game.getBall().getX(),
                game.getBall().getY(),
                20, 20
        );

        // Left paddle (Player 1 - A/Q keys)
        g.fillRect(
                GameSetting.getPadding(),
                game.getPlayer1Paddle().getY() - GameSetting.getPaddleHauteur() / 2,
                GameSetting.getPaddleWidth(),
                GameSetting.getPaddleHauteur()
        );

        // Right paddle (Player 2 - P/M keys)
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
        g.drawString("Controls: A/Q (P1)  P/M (P2)", GameSetting.getMapLength() / 2 - 80, 20);
    }

    public static void main(String[] args) {

        PongGameState game = new PongGameState();

        // Use getters from GameSetting
        int midY = GameSetting.getMapWidth() / 2;

        game.setPlayer1Paddle(new Paddle(1, midY));
        game.setPlayer2Paddle(new Paddle(2, midY));
        game.setBall(new Ball(GameSetting.getMapLength(), GameSetting.getMapWidth()));
        game.setScore1(new AtomicInteger(0));
        game.setScore2(new AtomicInteger(0));
        game.setUpdateLock(new ReentrantLock());

        JFrame frame = new JFrame("Pong Game - Controls: A/Q (P1) | P/M (P2)");
        LocalMain viewer = new LocalMain(game);
        frame.setContentPane(viewer);
        frame.setSize(GameSetting.getMapLength(), GameSetting.getMapWidth());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        long last = System.nanoTime();

        // Game loop
        while (true) {
            long now = System.nanoTime();
            double dt = (now - last) / 1_000_000_000.0;
            last = now;

            game.update(dt);

            try { Thread.sleep(16); } catch (Exception ignored) {}
        }
    }
}