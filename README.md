# Pong Game - Multiplayer Network Game

A Java implementation of the classic Pong game with both local and networked multiplayer support.

## Project Structure

```
src/
├── client/
│   └── PongClient.java      # Network client for multiplayer
├── game/
│   ├── Ball.java            # Ball physics and collision handling
│   ├── GameGraphic.java     # Single player graphics panel
│   ├── GameSetting.java     # Game configuration constants
│   ├── LocalMain.java       # Local 2-player game entry point
│   ├── Paddle.java          # Paddle movement and boundaries
│   ├── PongGameState.java   # Complete game state container
│   └── PongViewer.java      # Network client graphics panel
└── server/
    └── PongServer.java      # Game server for multiplayer

```

### Network Multiplayer
Play across a network with two separate computers.

**Start Server:**
```bash
java server.PongServer
```

**Start Client(s):**
```bash
# Connect to localhost
java client.PongClient

# Connect to specific server
java client.PongClient 192.168.1.100

# Connect to specific server and port
java client.PongClient 192.168.1.100 12345
```

**Controls (Network Mode):**
- Arrow Up / `A` : Move paddle up
- Arrow Down / `Q` : Move paddle down

## Game Settings

Edit `GameSetting.java` to customize:

| Setting | Default | Description |
|---------|---------|-------------|
| mapLength | 600 | Game width in pixels |
| mapWidth | 400 | Game height in pixels |
| paddleHauteur | 40 | Paddle height |
| paddleWidth | 10 | Paddle width |
| movement | 7 | Paddle movement per key press |
| baseSpeed | 150 | Initial ball speed |
| ballRadius | 5 | Ball collision radius |
| speedMultiplier | 1.1 | Speed increase per paddle hit |

## Building and Running

### Compile
```bash
javac -d out src/**/*.java
```

### Run Local Game
```bash
java -cp out game.LocalMain
```

### Run Network Server
```bash
java -cp out server.PongServer
```

### Run Network Client
```bash
java -cp out client.PongClient [server_address] [port]
```



## Network Protocol

The game uses Java Object Serialization over TCP/IP:

1. **Connection**: Client connects to server on port 12345
2. **Player ID**: Server sends player ID (1 or 2) to client
3. **Input**: Client sends integer commands (1=up, 0=down)
4. **State Updates**: Server broadcasts complete game state at ~60 FPS

## Features

- ✅ Local 2-player mode
- ✅ Network multiplayer (2 players)
- ✅ Paddle collision with angle adjustment
- ✅ Ball speed increases on paddle hits
- ✅ Score tracking
- ✅ Thread-safe game state
- ✅ Graceful disconnection handling

