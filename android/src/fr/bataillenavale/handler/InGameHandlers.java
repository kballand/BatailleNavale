package fr.bataillenavale.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.UUID;
import java.util.concurrent.Delayed;

import fr.bataillenavale.animation.AnimatedTexture;
import fr.bataillenavale.animation.AnimationManager;
import fr.bataillenavale.animation.Explosion;
import fr.bataillenavale.animation.Tir;
import fr.bataillenavale.animation.WaterTile;
import fr.bataillenavale.connection.ClientConnection;
import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.connection.ServerSenderReceiver;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.GameState;
import fr.bataillenavale.game.OceanChunk;
import fr.bataillenavale.game.Ship;
import fr.bataillenavale.game.ShipPart;
import fr.bataillenavale.models.ClientPlayerModel;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.EndGamePacket;
import fr.bataillenavale.packet.NextTurnPacket;
import fr.bataillenavale.packet.PlayerHasLostPacket;
import fr.bataillenavale.packet.ShipPartDestroyedPacket;
import fr.bataillenavale.packet.ShotTilePacket;
import fr.bataillenavale.packet.WaterTileDestroyedPacket;
import fr.bataillenavale.screens.MainMenuScreen;
import fr.bataillenavale.task.DelayedTask;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe gérant tous les packets recu durant la partie
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class InGameHandlers implements IHandler {
    @ClientHandler
    public static void nextTurnHandler(NextTurnPacket packet) {
        if (packet != null) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection instanceof ClientConnection) {
                ClientConnection clientConnection = (ClientConnection) connection;
                ClientPlayerModel player = clientConnection.getPlayer();
                if (player != null) {
                    final LobbyModel lobby = player.getLobby();
                    if (lobby != null) {
                        lobby.nextTurn();
                        PlayerModel playerTurn = lobby.getPlayerTurn();
                        if (playerTurn.equals(player)) {
                            BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                        } else {
                            BatailleNavale.getInstance().showText("C'est au tour de " + lobby.getPlayerTurn().getUsername() + " de jouer.", true);
                        }
                    }
                }
            }
        }
    }

    @ClientHandler
    public static void shipPartDestroyedHandler(final ShipPartDestroyedPacket packet) {
        if (packet != null) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection instanceof ClientConnection) {
                ClientConnection clientConnection = (ClientConnection) connection;
                final ClientPlayerModel player = clientConnection.getPlayer();
                if (player != null) {
                    final LobbyModel lobby = player.getLobby();
                    if (lobby != null) {
                        final ShipPart shipPart = packet.getShipPart();
                        if (shipPart != null) {
                            UUID touchedUUID = packet.getPlayerUUID();
                            if (touchedUUID != null) {
                                final PlayerModel touched = lobby.getPlayerByUUID(touchedUUID);
                                if (touched != null) {
                                    final int x = shipPart.getX();
                                    final int y = shipPart.getY();
                                    final OceanChunk touchedOcean = touched.getOceanChunk();
                                    if (touchedOcean != null) {
                                        lobby.resetPlayerTurn();
                                        Vector2 tileCenter = GameUtils.getTileCenter(lobby, touched, x, y);
                                        final Vector2 finalPosition = new Vector2(tileCenter.x - (GameConstants.ANIMATION_SIZE / 2), tileCenter.y + (GameConstants.ANIMATION_SIZE / 2));
                                        Tir tir = new Tir(new Vector2(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT), finalPosition, 0.08f);
                                        AnimationManager.getInstance().addAnimation(tir, new AnimatedTexture.OnAnimationFinishListener() {
                                            @Override
                                            public void run() {
                                                Explosion explosion = new Explosion(finalPosition, 0.1f, true);
                                                AnimationManager.getInstance().addAnimation(explosion, new AnimatedTexture.OnAnimationFinishListener() {
                                                    @Override
                                                    public void run() {
                                                        if(touched.equals(player)) {
                                                            for(Ship ship : player.getOceanChunk().getShips()) {
                                                                for(ShipPart part : ship.getShipParts()) {
                                                                    if(part.getX() == x && part.getY() == y) {
                                                                        part.setDestroyed(true);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        shipPart.setDestroyed(true);
                                                        touched.getOceanChunk().addDestroyedPart(shipPart);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ClientHandler
    public static void waterTileDestroyedHandler(WaterTileDestroyedPacket packet) {
        if (packet != null) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection instanceof ClientConnection) {
                ClientConnection clientConnection = (ClientConnection) connection;
                ClientPlayerModel player = clientConnection.getPlayer();
                if (player != null) {
                    final LobbyModel lobby = player.getLobby();
                    if (lobby != null) {
                        UUID touchedUUID = packet.getPlayerUUID();
                        if (touchedUUID != null) {
                            PlayerModel touched = lobby.getPlayerByUUID(touchedUUID);
                            if (touched != null) {
                                int x = packet.getX();
                                int y = packet.getY();
                                final OceanChunk touchedOcean = touched.getOceanChunk();
                                if (touchedOcean != null) {
                                    WaterTile[][] touchedTiles = touchedOcean.getWaterTiles();
                                    final WaterTile touchedTile = touchedTiles[x][y];
                                    lobby.resetPlayerTurn();
                                    Vector2 tileCenter = GameUtils.getTileCenter(lobby, touched, x, y);
                                    final Vector2 finalPosition = new Vector2(tileCenter.x - (GameConstants.ANIMATION_SIZE / 2), tileCenter.y + (GameConstants.ANIMATION_SIZE / 2));
                                    Tir tir = new Tir(new Vector2(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT), finalPosition, 0.08f);
                                    AnimationManager.getInstance().addAnimation(tir, new AnimatedTexture.OnAnimationFinishListener() {
                                        @Override
                                        public void run() {
                                            Explosion explosion = new Explosion(finalPosition, 0.1f, true);
                                            AnimationManager.getInstance().addAnimation(explosion, new AnimatedTexture.OnAnimationFinishListener() {
                                                @Override
                                                public void run() {
                                                    touchedTile.setDestroyed(true);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ClientHandler
    public static void gameEndsHandler(EndGamePacket packet) {
        if (packet != null) {
            String winnerUsername = packet.winner();
            if (winnerUsername != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection instanceof ClientConnection) {
                    ClientConnection clientConnection = (ClientConnection) connection;
                    ClientPlayerModel player = clientConnection.getPlayer();
                    if (player != null) {
                        final LobbyModel lobby = player.getLobby();
                        if (lobby != null) {
                            BatailleNavale.getInstance().showText(winnerUsername, true);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    BatailleNavale.getInstance().getScreen().dispose();
                                    BatailleNavale.getInstance().setScreen(new MainMenuScreen());
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @ServerHandler
    public static void shotTileHandler(ShotTilePacket packet, ServerSenderReceiver client) {
        if (packet != null && client != null) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection instanceof ServerConnection) {
                final ServerConnection server = (ServerConnection) connection;
                final ClientPlayerModel clientPlayerModel = server.getPlayer();
                if (clientPlayerModel != null) {
                    final LobbyModel lobby = clientPlayerModel.getLobby();
                    if (lobby != null && lobby.getState().equals(GameState.IN_GAME)) {
                        if (server.hasClient(client)) {
                            PlayerModel clientPlayer = client.getPlayer();
                            if (clientPlayer != null && clientPlayer.equals(lobby.getPlayerTurn())) {
                                UUID touchedUUID = packet.getPlayerUUID();
                                if (touchedUUID != null) {
                                    final PlayerModel touched = lobby.getPlayerByUUID(packet.getPlayerUUID());
                                    if (touched != null) {
                                        int x = packet.getX();
                                        int y = packet.getY();
                                        if (x >= 0 && x < GameConstants.OCEAN_CHUNK_SIZE && y >= 0 && y < GameConstants.OCEAN_CHUNK_SIZE) {
                                            final OceanChunk touchedOcean = touched.getOceanChunk();
                                            if (touchedOcean != null) {
                                                Ship[] touchedShips = touchedOcean.getShips();
                                                for (Ship touchedShip : touchedShips) {
                                                    for (final ShipPart shipPart : touchedShip.getShipParts()) {
                                                        if (shipPart.getX() == x && shipPart.getY() == y) {
                                                            if (!shipPart.isDestroyed()) {
                                                                server.sendPacket(new ShipPartDestroyedPacket(touchedUUID, shipPart));
                                                                lobby.resetPlayerTurn();
                                                                Vector2 tileCenter = GameUtils.getTileCenter(lobby, touched, x, y);
                                                                final Vector2 finalPosition = new Vector2(tileCenter.x - (GameConstants.ANIMATION_SIZE / 2), tileCenter.y + (GameConstants.ANIMATION_SIZE / 2));
                                                                Tir tir = new Tir(new Vector2(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT), finalPosition, 0.08f);
                                                                AnimationManager.getInstance().addAnimation(tir, new AnimatedTexture.OnAnimationFinishListener() {
                                                                    @Override
                                                                    public void run() {
                                                                        Explosion explosion = new Explosion(finalPosition, 0.1f, true);
                                                                        AnimationManager.getInstance().addAnimation(explosion, new AnimatedTexture.OnAnimationFinishListener() {
                                                                            @Override
                                                                            public void run() {
                                                                                shipPart.setDestroyed(true);
                                                                                touchedOcean.addDestroyedPart(shipPart);
                                                                                new DelayedTask(2000) {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        boolean hasLost = true;
                                                                                        for(Ship s : touchedOcean.getShips()) {
                                                                                            for(ShipPart sp : s.getShipParts()) {
                                                                                                if(!sp.isDestroyed()) {
                                                                                                    hasLost = false;
                                                                                                    break;
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        if(!hasLost) {
                                                                                            lobby.nextTurn();
                                                                                            PlayerModel playerTurn = lobby.getPlayerTurn();
                                                                                            if (playerTurn.equals(clientPlayerModel)) {
                                                                                                BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                                                                                            } else {
                                                                                                BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                                                                                            }
                                                                                            server.sendPacket(new NextTurnPacket());
                                                                                        } else {
                                                                                            touched.setLost(true);
                                                                                            BatailleNavale.getInstance().showText("Le joueur " + touched.getUsername() + " a perdu !", true);
                                                                                            server.sendPacket(new PlayerHasLostPacket(touched.getPlayerUUID()));
                                                                                            new DelayedTask(2000) {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    lobby.nextTurn();
                                                                                                    PlayerModel playerTurn = lobby.getPlayerTurn();
                                                                                                    if (playerTurn.equals(clientPlayerModel)) {
                                                                                                        BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                                                                                                    } else {
                                                                                                        BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                                                                                                    }
                                                                                                    server.sendPacket(new NextTurnPacket());
                                                                                                }
                                                                                            }.start();
                                                                                        }
                                                                                    }
                                                                                }.start();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                            return;
                                                        }
                                                    }
                                                }
                                                WaterTile[][] touchedTiles = touchedOcean.getWaterTiles();
                                                final WaterTile touchedTile = touchedTiles[x][y];
                                                if (!touchedTile.isDestroyed()) {
                                                    connection.sendPacket(new WaterTileDestroyedPacket(touchedUUID, x, y));
                                                    lobby.resetPlayerTurn();
                                                    Vector2 tileCenter = GameUtils.getTileCenter(lobby, touched, x, y);
                                                    final Vector2 finalPosition = new Vector2(tileCenter.x - (GameConstants.ANIMATION_SIZE / 2), tileCenter.y + (GameConstants.ANIMATION_SIZE / 2));
                                                    Tir tir = new Tir(new Vector2(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT), finalPosition, 0.08f);
                                                    AnimationManager.getInstance().addAnimation(tir, new AnimatedTexture.OnAnimationFinishListener() {
                                                        @Override
                                                        public void run() {
                                                            Explosion explosion = new Explosion(finalPosition, 0.1f, false);
                                                            AnimationManager.getInstance().addAnimation(explosion, new AnimatedTexture.OnAnimationFinishListener() {
                                                                @Override
                                                                public void run() {
                                                                    touchedTile.setDestroyed(true);
                                                                    new DelayedTask(2000) {
                                                                        @Override
                                                                        public void run() {
                                                                            lobby.nextTurn();
                                                                            PlayerModel playerTurn = lobby.getPlayerTurn();
                                                                            if (playerTurn.equals(clientPlayerModel)) {
                                                                                BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                                                                            } else {
                                                                                BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                                                                            }
                                                                            server.sendPacket(new NextTurnPacket());
                                                                        }
                                                                    }.start();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
