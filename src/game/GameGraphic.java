package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GameGraphic extends JPanel {
    private PongGameState game;
    private int playerID;

    public GameGraphic(PongGameState game, int playerID) {
        this.game = game;
        this.playerID = playerID;
        new Timer(16, e -> repaint()).start(); // repaint 60 fps

        // Enable keyboard focus
        setFocusable(true);
        requestFocusInWindow();

        // Add key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    handleKeyPress(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void handleKeyPress(KeyEvent e) throws IOException {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_A:
                // Player 1 up
                game.movePaddle(playerID, 1);
                System.out.println("Player "+playerID+"  up");
                break;
            case KeyEvent.VK_Q:
                // Player 1 down
                game.movePaddle(playerID, 0);
                System.out.println("Player "+playerID+" down");
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

}
