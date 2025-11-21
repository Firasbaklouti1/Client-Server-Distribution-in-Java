package server;


import game.PongGameState;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PongServer {
    private static final int PORT = 12345;
    private PongGameState gameState;
    private PlayerHandler[] players = new PlayerHandler[2];

    public PongServer() {
        gameState = new PongGameState();
        gameState.init(); // Initialise la balle, les paddles, etc.
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur Ping Pong démarré sur le port " + PORT);
            System.out.println("Attente des joueurs...");

            // Connexion Joueur 1
            Socket s1 = serverSocket.accept();
            System.out.println("Joueur 1 connecté !");
            players[0] = new PlayerHandler(s1, 1);
            players[0].start();

            // Connexion Joueur 2
            Socket s2 = serverSocket.accept();
            System.out.println("Joueur 2 connecté !");
            players[1] = new PlayerHandler(s2, 2);
            players[1].start();

            System.out.println("Le jeu commence !");
            gameLoop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gameLoop() {
        long last = System.nanoTime();

        while (true) {
            long now = System.nanoTime();
            double dt = (now - last) / 1_000_000_000.0;
            last = now;

            // 1. Mise à jour de la physique
            gameState.update(dt);

            // 2. Envoi de l'état aux joueurs
            broadcastState();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastState() {
        for (PlayerHandler p : players) {
            if (p != null) p.sendState(gameState);
        }
    }

    // Classe interne pour gérer chaque joueur
    private class PlayerHandler extends Thread {
        private Socket socket;
        private int playerId;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public PlayerHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
            try {
                // Toujours créer le Output avant le Input pour éviter le deadlock
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Envoyer l'ID au joueur pour qu'il sache qui il est
                out.writeInt(playerId);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendState(PongGameState state) {
            try {
                out.reset(); // CRUCIAL : Pour forcer l'envoi de l'objet mis à jour
                out.writeObject(state); // Utiliser writeUnshared de préférence
            } catch (Exception e) {
                // Gérer la déconnexion
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Lecture de la commande (int) : 1 = Haut, 0 = Bas
                    int direction = in.readInt();
                    gameState.movePaddle(playerId, direction);
                }
            } catch (Exception e) {
                System.out.println("Joueur " + playerId + " déconnecté.");
            }
        }
    }

    public static void main(String[] args) {
        new PongServer().start();
    }
}