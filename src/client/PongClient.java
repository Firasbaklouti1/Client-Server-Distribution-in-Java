package client;


import game.GameSetting;
import game.PongGameState;
import game.PongViewer;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PongClient{
    private String serverAddress = "localhost"; // Changer si sur un autre PC
    private int port = 12345;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private PongViewer viewer;
    private int myPlayerId;

    public PongClient() {
        try {
            socket = new Socket(serverAddress, port);
            // Output avant Input
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 1. Recevoir son ID (1 ou 2)
            myPlayerId = in.readInt();
            System.out.println("Connecté en tant que Joueur " + myPlayerId);

            setupGUI();
            startListening();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        JFrame frame = new JFrame("Pong - Player " + myPlayerId);
        viewer = new PongViewer(myPlayerId);

        // Gestion des touches : on envoie juste la commande au serveur
        viewer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    int keyCode = e.getKeyCode();
                    // Commandes génériques (A/Q ou Flèches ou P/M)
                    // On envoie 1 pour MONTER, 0 pour DESCENDRE
                    if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_P) {
                        out.writeInt(1); // UP
                        out.flush();
                    } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_M) {
                        out.writeInt(0); // DOWN
                        out.flush();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setContentPane(viewer);
        frame.setSize(GameSetting.getMapLength(), GameSetting.getMapWidth() + 40); // +40 pour la barre de titre
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void startListening() {
        try {
            while (true) {
                // Lire l'objet GameState envoyé par le serveur
                PongGameState state = (PongGameState) in.readObject();

                // Mettre à jour l'interface graphique
                viewer.updateGame(state);
            }
        } catch (Exception e) {
            System.out.println("Connexion au serveur perdue.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new PongClient();
    }
}
