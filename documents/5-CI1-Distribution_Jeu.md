

Programmation Concurrente et Distribuée
Distribution d’un Jeu Multijoueur
## Khaled Barbaria
khaled.barbaria@ensta.org
Faculté des Sciences de Bizerte
Cycle Ingénieur, 1ère année
## 2025-2026
Dernière modification : 12-11-2025 10 :12
Distribution d’un Jeu Multijoueur1 / 29

Table des Matières
## 1
Introduction aux Systèmes Distribués
## 2
## Sockets Java
## 3
Threads et Concurrence
## 4
Synchronisation et Thread Safety
## 5
Protocole de Communication
## 6
## Architecture Complète
## 7
Optimisations et Patterns Avancés
## 8
Gestion des Erreurs
## 9
## Exemple Pratique : Jeu Pong
## 10
Tests et Debugging
## 11
## Conclusion
Distribution d’un Jeu Multijoueur2 / 29

Objectifs du Cours
Comprendre l’architecture client-serveur pour les jeux
Maîtriser la programmation réseau avec sockets TCP/UDP
Gérer la concurrence avec les threads Java
Synchroniser l’état du jeu entre multiples clients
Implémenter des patterns de communication efficaces
Distribution d’un Jeu MultijoueurIntroduction3 / 29

Architecture d’un Jeu Distribué
## Serveur
de Jeu
(Game
## Server)
Client 1Client 2
## Client 3
## Actions
## État
Distribution d’un Jeu MultijoueurIntroduction4 / 29

Introduction aux Sockets
Socket : Point de terminaison pour la communication réseau
Deux types principaux :
TCP Sockets : Connexion fiable, orientée flux (Stream)
UDP Sockets : Non connecté, orienté datagramme
## Classes Java :
ServerSocket : Serveur TCP
Socket : Client TCP
DatagramSocket : UDP
Distribution d’un Jeu MultijoueurSockets Java5 / 29

Serveur TCP Basique
1 public class GameServer {
2     private ServerSocket serverSocket;
3     private static final int PORT = 8080;
## 4
5     public void start() throws IOException {
6         serverSocket = new ServerSocket(PORT);
7         System.out.println("Serveur démarré sur port " + PORT);
## 8
9         while (true) {
10             Socket clientSocket = serverSocket.accept();
11             System.out.println("Nouveau client connecté");
## 12
13             // Créer un thread pour gérer ce client
14             ClientHandler handler =
15                 new ClientHandler(clientSocket);
16             new Thread(handler).start();
## 17         }
## 18     }
## 19 }
Distribution d’un Jeu MultijoueurSockets Java6 / 29

Client TCP Basique
1 public class GameClient {
2     private Socket socket;
3     private PrintWriter out;
4     private BufferedReader in;
## 5
6     public void connect(String host, int port)
7         throws IOException {
8         socket = new Socket(host, port);
9         out = new PrintWriter(
10             socket.getOutputStream(), true);
11         in = new BufferedReader(
12             new InputStreamReader(socket.getInputStream()));
## 13
14         System.out.println("Connecté au serveur");
## 15     }
## 16
17     public void sendAction(String action) {
18         out.println(action);
## 19     }
## 20 }
Distribution d’un Jeu MultijoueurSockets Java7 / 29

Pourquoi les Threads ?
Besoins d’un jeu multijoueur :
Gérer plusieurs clients simultanément
Recevoir et envoyer des messages en parallèle
Mettre à jour l’état du jeu périodiquement
Maintenir la réactivité de l’interface
Solution : Programmation multithread
Un thread par client connecté
Thread de mise à jour du jeu (game loop)
Thread de broadcast pour diffuser l’état
Distribution d’un Jeu MultijoueurThreads et Concurrence8 / 29

Gestionnaire de Client (Thread)
1  public class ClientHandler implements Runnable {
2      private Socket socket;
3      private GameState gameState;
4      private String playerId;
5      private BufferedReader in;
6      private PrintWriter out;
## 7
8      public ClientHandler(Socket socket, GameState state) {
9          this.socket = socket;
10          this.gameState = state;
## 11      }
## 12
13      @Override
14      public void run() {
15          try {
16              setupStreams();
17              handleClient();
18          } catch (IOException e) {
19              e.printStackTrace();
20          } finally {
21              cleanup();
## 22          }
## 23      }
## 24  }
Distribution d’un Jeu MultijoueurThreads et Concurrence9 / 29

Boucle de Traitement Client
1  private void handleClient() throws IOException {
2      String message;
3      while ((message = in.readLine()) != null) {
4          // Parser le message
5          Action action = parseAction(message);
## 6
7          // Synchroniser l’accès à l’état du jeu
8          synchronized (gameState) {
9              gameState.applyAction(playerId, action);
## 10          }
## 11
12          // Notifier les autres joueurs
13          broadcastGameState();
## 14
15          if (message.equals("QUIT")) {
16              break;
## 17          }
## 18      }
## 19  }
Distribution d’un Jeu MultijoueurThreads et Concurrence10 / 29

Problèmes de Concurrence
## Race Conditions :
Plusieurs threads modifient l’état du jeu simultanément
Lecture/écriture concurrente des positions des joueurs
Corruption de données
## Solutions :
synchronized : Blocs et méthodes synchronisées
ReentrantLock : Verrous explicites
ConcurrentHashMap : Collections thread-safe
AtomicInteger/AtomicReference : Opérations atomiques
Distribution d’un Jeu MultijoueurSynchronisation et Thread Safety11 / 29

État de Jeu Thread-Safe
1  public class GameState {
2      private final ConcurrentHashMap<String, Player> players;
3      private final ReentrantReadWriteLock lock;
4      private volatile boolean gameRunning;
## 5
6      public GameState() {
7          players = new ConcurrentHashMap<>();
8          lock = new ReentrantReadWriteLock();
9          gameRunning = true;
## 10      }
## 11
12      public void updatePlayerPosition(String id, int x, int y) {
13          lock.writeLock().lock();
14          try {
15              Player player = players.get(id);
16              if (player != null) {
17                  player.setPosition(x, y);
## 18              }
19          } finally {
20              lock.writeLock().unlock();
## 21          }
## 22      }
## 23  }
Distribution d’un Jeu MultijoueurSynchronisation et Thread Safety12 / 29

Pattern Producer-Consumer
1  public class MessageQueue {
2      private final BlockingQueue<GameMessage> queue;
## 3
4      public MessageQueue() {
5          queue = new LinkedBlockingQueue<>(100);
## 6      }
## 7
8      // Producer : ajouter un message
9      public void enqueue(GameMessage msg)
10          throws InterruptedException {
11          queue.put(msg);
## 12      }
## 13
14      // Consumer : récupérer un message
15      public GameMessage dequeue()
16          throws InterruptedException {
17          return queue.take();
## 18      }
## 19  }
Distribution d’un Jeu MultijoueurSynchronisation et Thread Safety13 / 29

Design du Protocole
Format des messages :
CONNECT|playerName
MOVE|x|y
ACTION|actionType|data
STATE|jsonData
## DISCONNECT
## Considérations :
Délimiteur de message (newline, longueur, etc.)
Sérialisation (JSON, Protocol Buffers, custom)
Compression pour réduire la bande passante
Heartbeat pour détecter les déconnexions
Distribution d’un Jeu MultijoueurProtocole de Communication14 / 29

Sérialisation JSON
1  public class GameMessage {
2      private String type;
3      private String playerId;
4      private Map<String, Object> data;
## 5
6      public String toJson() {
7          // Utiliser Gson ou Jackson
8          Gson gson = new Gson();
9          return gson.toJson(this);
## 10      }
## 11
12      public static GameMessage fromJson(String json) {
13          Gson gson = new Gson();
14          return gson.fromJson(json, GameMessage.class);
## 15      }
## 16
17      // Exemple d’utilisation
18      // out.println(message.toJson());
## 19  }
Distribution d’un Jeu MultijoueurProtocole de Communication15 / 29

Serveur de Jeu Complet (1/2)
1  public class MultiplayerGameServer {
2      private ServerSocket serverSocket;
3      private GameState gameState;
4      private List<ClientHandler> clients;
5      private ExecutorService threadPool;
6      private volatile boolean running;
## 7
8      public MultiplayerGameServer(int port) throws IOException {
9          serverSocket = new ServerSocket(port);
10          gameState = new GameState();
11          clients = new CopyOnWriteArrayList<>();
12          threadPool = Executors.newCachedThreadPool();
13          running = true;
## 14      }
## 15
16      public void start() {
17          startGameLoop();
18          acceptClients();
## 19      }
## 20  }
Distribution d’un Jeu MultijoueurArchitecture Complète16 / 29

Serveur de Jeu Complet (2/2)
1  private void acceptClients() {
2      while (running) {
3          try {
4              Socket client = serverSocket.accept();
5              ClientHandler handler =
6                  new ClientHandler(client, gameState, this);
7              clients.add(handler);
8              threadPool.execute(handler);
9          } catch (IOException e) {
10              if (running) e.printStackTrace();
## 11          }
## 12      }
## 13  }
## 14
15  private void startGameLoop() {
16      threadPool.execute(() -> {
17          while (running) {
18              gameState.update();
19              broadcastState();
20              sleep(50); // 20 TPS
## 21          }
## 22      });
## 23  }
Distribution d’un Jeu MultijoueurArchitecture Complète17 / 29

Broadcast de l’État
1  public void broadcastState() {
2      String stateJson = gameState.toJson();
3      GameMessage msg = new GameMessage("STATE", stateJson);
## 4
5      for (ClientHandler client : clients) {
6          try {
7              client.send(msg);
8          } catch (IOException e) {
9              // Client déconnecté
10              removeClient(client);
## 11          }
## 12      }
## 13  }
## 14
15  public void broadcastToOthers(ClientHandler sender,
16                                GameMessage msg) {
17      for (ClientHandler client : clients) {
18          if (client != sender) {
19              client.send(msg);
## 20          }
## 21      }
## 22  }
Distribution d’un Jeu MultijoueurArchitecture Complète18 / 29

## Optimisations Réseau
Réduction de la latence :
Client-side prediction
Server reconciliation
Interpolation d’entités
Dead reckoning
Efficacité de la bande passante :
Envoyer seulement les deltas (changements)
Compression des données
Prioritisation des messages
Agrégation de messages
Distribution d’un Jeu MultijoueurOptimisations et Patterns Avancés19 / 29

## Delta Compression
1  public class StateSnapshot {
2      private long timestamp;
3      private Map<String, PlayerState> states;
## 4
5      public StateDelta computeDelta(StateSnapshot previous) {
6          StateDelta delta = new StateDelta(timestamp);
## 7
8          for (String id : states.keySet()) {
9              PlayerState current = states.get(id);
10              PlayerState prev = previous.states.get(id);
## 11
12              if (prev == null || !current.equals(prev)) {
13                  delta.addChange(id, current);
## 14              }
## 15          }
## 16
17          return delta;
## 18      }
## 19  }
Distribution d’un Jeu MultijoueurOptimisations et Patterns Avancés20 / 29

Patterns de Concurrence Avancés
## Thread Pool Pattern :
Réutilisation des threads
Limitation des ressources
ExecutorService
## Actor Model :
Isolation des états
Communication par messages
Akka framework
## Reactive Programming :
Streams d’événements
## Backpressure
RxJava, Project Reactor
Distribution d’un Jeu MultijoueurOptimisations et Patterns Avancés21 / 29

Scénarios d’Erreur
Problèmes réseau :
Perte de connexion
## Timeout
Messages corrompus
Stratégies de gestion :
Heartbeat/ping-pong pour détecter les déconnexions
Timeout configurables
Reconnexion automatique côté client
Message queues pour garantir la livraison
Logging et monitoring
Distribution d’un Jeu MultijoueurGestion des Erreurs22 / 29

## Heartbeat Implementation
1  public class HeartbeatMonitor implements Runnable {
2      private ClientHandler client;
3      private long lastHeartbeat;
4      private static final long TIMEOUT = 30000; // 30s
## 5
6      @Override
7      public void run() {
8          while (client.isConnected()) {
9              long elapsed = System.currentTimeMillis()
10                           - lastHeartbeat;
## 11
12              if (elapsed > TIMEOUT) {
13                  client.disconnect("Timeout");
14                  break;
## 15              }
## 16
17              client.sendPing();
18              sleep(5000);
## 19          }
## 20      }
## 21  }
Distribution d’un Jeu MultijoueurGestion des Erreurs23 / 29

Architecture du Pong Multijoueur
État synchronisé
## Joueur 1
## MOVE UP/DOWN
## Joueur 2
## MOVE UP/DOWN
Messages échangés :
PADDLE_MOVE|player|y
BALL_STATE|x|y|vx|vy
SCORE|player1Score|player2Score
Distribution d’un Jeu MultijoueurExemple Pratique : Jeu Pong24 / 29

État du Jeu Pong
1  public class PongGameState {
2      private Ball ball;
3      private Paddle player1Paddle;
4      private Paddle player2Paddle;
5      private AtomicInteger score1, score2;
6      private ReentrantLock updateLock;
## 7
8      public void update(double deltaTime) {
9          updateLock.lock();
10          try {
11              ball.update(deltaTime);
12              checkCollisions();
13              checkScoring();
14          } finally {
15              updateLock.unlock();
## 16          }
## 17      }
## 18
19      public void movePaddle(int playerId, int direction) {
20          // Thread-safe paddle movement
## 21      }
## 22  }
Distribution d’un Jeu MultijoueurExemple Pratique : Jeu Pong25 / 29

Stratégies de Test
Tests unitaires :
Test de la logique du jeu isolément
Mock des sockets et streams
JUnit et Mockito
Tests d’intégration :
Simulation de clients multiples
Vérification de la synchronisation
Tests de charge
## Debugging :
Logging structuré (SLF4J, Log4j)
Thread dumps pour deadlocks
Profiling (VisualVM, JProfiler)
Distribution d’un Jeu MultijoueurTests et Debugging26 / 29

Test de Charge
1  @Test
2  public void testMultipleClients() throws Exception {
3      GameServer server = new GameServer(8080);
4      server.start();
## 5
6      List<GameClient> clients = new ArrayList<>();
## 7
8      // Créer 100 clients
9      for (int i = 0; i < 100; i++) {
10          GameClient client = new GameClient();
11          client.connect("localhost", 8080);
12          clients.add(client);
## 13      }
## 14
15      // Envoyer des actions simultanément
16      ExecutorService executor =
17          Executors.newFixedThreadPool(100);
18      // ... test logic
## 19
20      assertEquals(100, server.getConnectedClients());
## 21  }
Distribution d’un Jeu MultijoueurTests et Debugging27 / 29

## Points Clés
## 1
Sockets : Communication réseau client-serveur
## 2
Threads : Gestion de la concurrence
## 3
Synchronisation : Protection de l’état partagé
## 4
Protocole : Design de messages efficaces
## 5
Optimisation : Latence et bande passante
## 6
Robustesse : Gestion d’erreurs et reconnexion
## Best Practices :
Toujours synchroniser l’accès aux données partagées
Utiliser des thread pools plutôt que créer des threads
Implémenter un protocole clair et versionné
Tester avec de multiples clients simultanés
Distribution d’un Jeu MultijoueurConclusion28 / 29

Ressources et Références
## Documentation Java :
Java Concurrency Tutorial (Oracle)
## Java Networking Tutorial
Effective Java (Joshua Bloch)
## Frameworks :
Netty : Framework réseau asynchrone
Kryonet : Networking pour jeux Java
Distribution d’un Jeu MultijoueurConclusion29 / 29