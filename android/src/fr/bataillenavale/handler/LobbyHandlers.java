package fr.bataillenavale.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.util.UUID;

import fr.bataillenavale.connection.ClientConnection;
import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.connection.ServerSenderReceiver;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.GameState;
import fr.bataillenavale.models.ClientPlayerModel;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.ChatMessagePacket;
import fr.bataillenavale.packet.JoinLobbyInformationPacket;
import fr.bataillenavale.packet.JoinLobbyRequestPacket;
import fr.bataillenavale.packet.KickPlayerPacket;
import fr.bataillenavale.packet.LoadingStartsPacket;
import fr.bataillenavale.packet.LobbyFullPacket;
import fr.bataillenavale.packet.PlayerJoinLobbyPacket;
import fr.bataillenavale.packet.PlayerLeaveLobbyPacket;
import fr.bataillenavale.packet.PlayerUsernameChangePacket;
import fr.bataillenavale.packet.ReadyStateChangePacket;
import fr.bataillenavale.screens.LoadingScreen;
import fr.bataillenavale.screens.LobbyScreen;
import fr.bataillenavale.screens.MainMenuScreen;

/**
 * Classe gérant tous les packets recu dans le salon
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class LobbyHandlers implements IHandler {

	/**
     * Reception d'un message par le client
     */
	@ClientHandler
    public static void clientChatMessageHandler(ChatMessagePacket packet) {
        if (packet != null) {
            String message = packet.getMessage();
            if (message != null && message.length() > 0 && message.trim().length() > 0) {
                Screen currentScreen = BatailleNavale.getInstance().getScreen();
                if (currentScreen instanceof LobbyScreen) {
                    LobbyScreen lobbyScreen = (LobbyScreen) currentScreen;
                    lobbyScreen.addMessage(message, !packet.getPlayerUUID().toString().equals(ConnectionManager.getInstance().getConnection().getPlayer().getPlayerUUID().toString()));
                }
            }
        }
    }

	/**
     * Envoi d'un message depuis le serveur
     */
    @ServerHandler
    public static void serverChatMessageHandler(ChatMessagePacket packet, ServerSenderReceiver client) {
        if (packet != null && client != null) {
            PlayerModel clientPlayer = client.getPlayer();
            if (clientPlayer != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection != null) {
                    ClientPlayerModel player = connection.getPlayer();
                    if (player != null) {
                        LobbyModel lobby = player.getLobby();
                        if (lobby != null && lobby.hasPlayer(clientPlayer) && lobby.getState().equals(GameState.IN_LOBBY)) {
                            String message = packet.getMessage();
                            if (message != null && message.length() > 0 && message.trim().length() > 0) {
                                Screen currentScreen = BatailleNavale.getInstance().getScreen();
                                if (currentScreen instanceof LobbyScreen) {
                                    LobbyScreen lobbyScreen = (LobbyScreen) currentScreen;
                                    lobbyScreen.addMessage(message, true);
                                    connection.sendPacket(packet);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gere l'acces a un salon
     */
    @ServerHandler
    public static void joinLobbyRequestHandler(JoinLobbyRequestPacket packet, ServerSenderReceiver client) {
        if (packet != null && client != null) {
            boolean success = false;
            ServerConnection server = (ServerConnection) ConnectionManager.getInstance().getConnection();
            String username = packet.getUsername();
            UUID playerUUID = packet.getPlayerUUID();
            if (!server.hasClient(client) && username != null && playerUUID != null) {
                LobbyModel lobby = server.getPlayer().getLobby();
                if (lobby != null) {
                    if (!lobby.isFull()) {
                        PlayerModel player = new PlayerModel(username, false, playerUUID);
                        client.initPlayer(player);
                        lobby.addPlayer(player);
                        server.sendPacket(new PlayerJoinLobbyPacket(player));
                        server.addClient(client);
                        client.send(new JoinLobbyInformationPacket(lobby));
                        BatailleNavale.getInstance().showText("Le joueur " + username + " a rejoint le salon !", false);
                        success = true;
                    } else {
                        client.send(new LobbyFullPacket());
                    }
                }
            }
            if (!success) {
                client.close();
            }
        }
    }

    /**
     * Notifie les joueurs qu'un nouveau joueur a rejoint
     */
    @ClientHandler
    public static void playerJoinLobbyHandler(PlayerJoinLobbyPacket packet) {
        if (packet != null) {
            PlayerModel player = packet.getPlayer();
            if (player != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection != null) {
                    ClientPlayerModel clientPlayer = connection.getPlayer();
                    LobbyModel lobby = clientPlayer.getLobby();
                    if (lobby != null) {
                        lobby.addPlayer(player);
                        BatailleNavale.getInstance().showText("Le joueur " + player.getUsername() + " a rejoint le salon !", false);
                    }
                }
            }
        }
    }

    /**
     * Notifie les joueurs qu'un joueur a quitte le salon
     */
    @ClientHandler
    public static void playerLeaveLobbyHandler(PlayerLeaveLobbyPacket packet) {
        if (packet != null) {
            UUID playerUUID = packet.getPlayerUUID();
            if (playerUUID != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection != null) {
                    ClientPlayerModel clientPlayer = connection.getPlayer();
                    LobbyModel lobby = clientPlayer.getLobby();
                    if (lobby != null) {
                        PlayerModel player = lobby.getPlayerByUUID(playerUUID);
                        if (player != null) {
                            lobby.removePlayer(player);
                            BatailleNavale.getInstance().showText("Le joueur " + player.getUsername() + " a quitté le salon !", false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gere les informations d'un salon
     */
    @ClientHandler
    public static void joinLobbyInformationHandler(JoinLobbyInformationPacket packet) {
        if (packet != null) {
            final LobbyModel lobby = packet.getLobby();
            if (lobby != null) {
                ConnectionManager.getInstance().getLobbyList().clearAvailableLobby();
                ConnectionManager.getInstance().stopLobbyDiscovery();
                ConnectionManager.getInstance().getConnection().getPlayer().setLobby(lobby);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        BatailleNavale.getInstance().getScreen().dispose();
                        BatailleNavale.getInstance().setScreen(new LobbyScreen("Vous avez rejoint le salon.", lobby));
                    }
                });
            }
        }
    }

    /**
     * Gere un salon plein
     */
    @ClientHandler
    public static void lobbyFullHandler(LobbyFullPacket packet) {
        if (packet != null) {
            BatailleNavale.getInstance().showText("Ce salon est plein !", false);
            ConnectionManager.getInstance().closeConnection();
        }
    }

    /**
     * Regarde si de sjoueurs se mettent en etat "pret" au niveau serveur
     */
    @ServerHandler
    public static void serverReadyStateChangeHandler(ReadyStateChangePacket packet, ServerSenderReceiver client) {
        if (packet != null && client != null) {
            UUID playerUUID = packet.getPlayerUUID();
            if (playerUUID != null) {
                String uuid = playerUUID.toString();
                PlayerModel clientPlayer = client.getPlayer();
                if (clientPlayer != null && clientPlayer.getPlayerUUID().toString().equals(uuid)) {
                    Connection connection = ConnectionManager.getInstance().getConnection();
                    if (connection != null) {
                        connection.sendPacket(new ReadyStateChangePacket(playerUUID));
                        clientPlayer.setReady(!clientPlayer.isReady());
                    }
                }
            }
        }
    }

    /**
     * Regarde si de sjoueurs se mettent en etat "pret" au niveau des joueurs
     */
    @ClientHandler
    public static void clientReadyStateChangeHandler(ReadyStateChangePacket packet) {
        if (packet != null) {
            UUID playerUUID = packet.getPlayerUUID();
            if (playerUUID != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection != null) {
                    ClientPlayerModel player = connection.getPlayer();
                    if (player != null) {
                        LobbyModel lobby = player.getLobby();
                        if (lobby != null) {
                            PlayerModel readyPlayer = lobby.getPlayerByUUID(playerUUID);
                            if (readyPlayer != null) {
                                readyPlayer.setReady(!readyPlayer.isReady());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gere l'expulsion d'un joueur
     */
    @ClientHandler
    public static void kickPlayerHandler(KickPlayerPacket packet) {
        if (packet != null) {
            UUID playerUUID = packet.getPlayerUUID();
            if (playerUUID != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection != null) {
                    ClientPlayerModel player = connection.getPlayer();
                    if (player != null) {
                        LobbyModel lobby = player.getLobby();
                        if (lobby != null) {
                            PlayerModel kickedPlayer = lobby.getPlayerByUUID(playerUUID);
                            if (kickedPlayer != null) {
                                if (kickedPlayer.equals(player)) {
                                    BatailleNavale.getInstance().showText("Vous avez été expulsé du salon !", false);
                                    ConnectionManager.getInstance().closeConnection();
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            BatailleNavale.getInstance().getScreen().dispose();
                                            ConnectionManager.getInstance().closeConnection();
                                            BatailleNavale.getInstance().setScreen(new MainMenuScreen());
                                        }
                                    });
                                } else {
                                    BatailleNavale.getInstance().showText("Le joueur " + player.getUsername() + " a été exclu du salon !", false);
                                    lobby.removePlayer(kickedPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gere le changement de nom d'un joueur
     */
    @ClientHandler
    public static void clientPlayerUsernameChangeHandler(PlayerUsernameChangePacket packet) {
        if (packet != null) {
            UUID playerUUID = packet.getPlayerUUID();
            if (playerUUID != null) {
                String newUsername = packet.getNewUsername();
                if (newUsername != null && !newUsername.trim().isEmpty()) {
                    Connection connection = ConnectionManager.getInstance().getConnection();
                    if (connection != null) {
                        ClientPlayerModel clientPlayer = connection.getPlayer();
                        if (clientPlayer != null) {
                            LobbyModel lobby = clientPlayer.getLobby();
                            if (lobby != null) {
                                PlayerModel player = lobby.getPlayerByUUID(playerUUID);
                                if (player != null) {
                                    if (!player.equals(clientPlayer)) {
                                        BatailleNavale.getInstance().showText(player.getUsername() + " a changé son pseudo en " + newUsername + ".", false);
                                    }
                                    player.setUsername(newUsername);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gere le changement de nom de l'hote
     */
    @ServerHandler
    public static void serverPlayerUsernameChangeHandler(PlayerUsernameChangePacket packet, ServerSenderReceiver client) {
        if (packet != null && client != null) {
            UUID playerUUID = packet.getPlayerUUID();
            if (playerUUID != null) {
                String newUsername = packet.getNewUsername();
                if (newUsername != null && !newUsername.trim().isEmpty()) {
                    Connection connection = ConnectionManager.getInstance().getConnection();
                    if (connection instanceof ServerConnection) {
                        ClientPlayerModel serverPlayer = connection.getPlayer();
                        if (serverPlayer != null) {
                            LobbyModel lobby = serverPlayer.getLobby();
                            if (lobby != null && lobby.getState().equals(GameState.IN_LOBBY)) {
                                PlayerModel player = lobby.getPlayerByUUID(playerUUID);
                                if (player != null && player.equals(client.getPlayer())) {
                                    BatailleNavale.getInstance().showText(player.getUsername() + " a changé son pseudo en " + newUsername + ".", false);
                                    player.setUsername(newUsername);
                                    connection.sendPacket(packet);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gere le lancement de la partie
     */
    @ClientHandler
    public static void loadingStartsHandler(LoadingStartsPacket packet) {
        if (packet != null) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection instanceof ClientConnection) {
                ClientConnection clientConnection = (ClientConnection) connection;
                ClientPlayerModel player = clientConnection.getPlayer();
                if (player != null) {
                    LobbyModel lobby = player.getLobby();
                    if (lobby != null && lobby.getState().equals(GameState.IN_LOBBY)) {
                        lobby.setState(GameState.IN_LOADING);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                BatailleNavale.getInstance().getScreen().dispose();
                                BatailleNavale.getInstance().setScreen(new LoadingScreen());
                            }
                        });
                    }
                }
            }
        }
    }
}
