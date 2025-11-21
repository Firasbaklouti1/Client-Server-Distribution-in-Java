# Client-Server-Distribution-in-Java

This project demonstrates how to transform a local Java game (Pong) into
a distributed application using TCP Sockets and Java Object
Serialization. The core focus is on establishing a robust Authoritative
Server architecture where the game logic is centralized, and clients act
as thin input/output terminals.

# Project Architecture

The application follows a standard Client-Server model with a critical
distinction: the server is authoritative.

🖥️ 1. The Authoritative Server (PongServer) The server is the single
source of truth for the game state.

Role: Manages the central game state (PongGameState), executes the game
loop, calculates all physics (ball movement, collisions), and tracks
scoring.

Communication Flow (Server to Client): It continuously sends the entire,
updated PongGameState object to both connected clients at a high
frequency (around 60 times per second).

Communication Flow (Client to Server): It listens for and processes
movement commands (UP/DOWN) from both clients.

🎮 2. The Thin Clients (PongClient & PongViewer) The client side is
responsible solely for interaction and display.

PongClient: Handles the network connection, receives the assigned Player
ID (1 or 2), sends input commands (integers) to the server, and receives
the updated PongGameState object.

PongViewer: The Graphical User Interface (Swing). It is a "dumb
terminal" that simply renders the game state it receives from the
PongClient. It contains no game logic (no physics, no collision checks).

# Key Distribution Mechanisms

The success of the distribution relies on Java's network programming
features:

1.  Object Serialization\
    The core object, PongGameState, implements the Serializable
    interface. This allows the entire state (ball position, paddle
    positions, scores) to be bundled and transmitted across the network
    efficiently using ObjectOutputStream and ObjectInputStream.

2.  Synchronization and Immutability\
    The server's central state management guarantees that all clients
    see the exact same game state simultaneously, preventing cheating
    and desynchronization issues (the "single source of truth").

The ReentrantLock field inside PongGameState is marked as transient to
ensure it is not serialized and does not crash the network transmission
process.

3.  Preventing Network Caching (.reset())\
    In the server's main loop, the following method is crucial:

```{=html}
<!-- -->
```
    out.reset();
    out.writeUnshared(state);

The ObjectOutputStream maintains a cache of object references it has
already sent. Because the server is continuously sending the same
instance of the PongGameState (just with updated internal values),
calling reset() forces the stream to discard the old reference and
re-serialize the entire object with its current, updated values,
ensuring the client receives the movement.

# Getting Started

Follow these steps to set up and run the distributed Pong game.

📋 **Prerequisites**\
Java Development Kit (JDK) 8 or higher.

## Step 1: Launch the Server

Compile all Java files.

Run the server application:

    java game.PongServer

The server will initialize and wait for two connections:

    Serveur Ping Pong démarré sur le port 12345
    Attente des joueurs...

## Step 2: Launch the Clients (Two Instances)

Open two separate terminal windows or run two instances via your IDE.

Launch Client 1:

    java game.PongClient

The server will register "Joueur 1 connecté !"

Launch Client 2:

    java game.PongClient

The server will register "Joueur 2 connecté !" and the game will start.

## ⌨️ Default Controls

**Player 1 (Left Paddle):**\
Move Up: A\
Move Down: Q

**Player 2 (Right Paddle):**\
Move Up: P\
Move Down: M

# 🌐 Configuration

Default Port: 12345\
Default Address: localhost

To run the game across different machines, you must modify the
`serverAddress` variable in the `game/PongClient.java` file to point to
the IP address of the machine running the PongServer.
