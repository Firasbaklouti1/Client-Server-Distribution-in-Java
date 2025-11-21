package game;

import javax.swing.*;
import java.awt.*;

public class PongViewer extends JPanel {
    private PongGameState game;
    private int myPlayerId; // Pour afficher "You are Player X"

    public PongViewer(int myPlayerId) {
        this.myPlayerId = myPlayerId;
        this.game = new PongGameState(); // Etat vide au départ
        setFocusable(true);
        requestFocusInWindow();

        // Le KeyListener sera ajouté dans le PongClient, pas ici,
        // pour séparer la logique réseau de l'affichage.
    }

    public void updateGame(PongGameState newGame) {
        this.game = newGame;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (game == null || game.getBall() == null) return;

        // Fond
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Objets en blanc
        g.setColor(Color.WHITE);

        // Balle
        g.fillOval(game.getBall().getX(), game.getBall().getY(), 20, 20);

        // Paddle 1
        g.fillRect(GameSetting.getPadding(),
                game.getPlayer1Paddle().getY() - GameSetting.getPaddleHauteur() / 2,
                GameSetting.getPaddleWidth(), GameSetting.getPaddleHauteur());

        // Paddle 2
        g.fillRect(GameSetting.getMapLength() - GameSetting.getPadding() - GameSetting.getPaddleWidth(),
                game.getPlayer2Paddle().getY() - GameSetting.getPaddleHauteur() / 2,
                GameSetting.getPaddleWidth(), GameSetting.getPaddleHauteur());

        // Scores
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Player 1: " + game.getScore1().get(), 50, 30);
        g.drawString("Player 2: " + game.getScore2().get(), GameSetting.getMapLength() - 150, 30);

        // Info Joueur
        g.setColor(Color.YELLOW);
        g.drawString("You are Player " + myPlayerId, GameSetting.getMapLength() / 2 - 50, 400);
    }
}