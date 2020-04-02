package fr.bataillenavale.handler;

import com.badlogic.gdx.Gdx;

import fr.bataillenavale.connection.ClientConnection;
import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.connection.ServerSenderReceiver;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.GameState;
import fr.bataillenavale.game.OceanChunk;
import fr.bataillenavale.models.ClientPlayerModel;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.GameStartsPacket;
import fr.bataillenavale.packet.LoadingFinishedPacket;
import fr.bataillenavale.screens.GameScreen;

/**
 * Classe gérant tous les packets recu durant le chargement
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class LoadingHandlers implements IHandler {
    @ServerHandler
    public static void loadingFinishedHandler(LoadingFinishedPacket packet, ServerSenderReceiver client) {
        if (packet != null && client != null) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection instanceof ServerConnection) {
                ServerConnection server = (ServerConnection) connection;
                ClientPlayerModel clientPlayerModel = server.getPlayer();
                if (clientPlayerModel != null) {
                    LobbyModel lobby = clientPlayerModel.getLobby();
                    if (lobby != null && lobby.getState().equals(GameState.IN_LOADING)) {
                        if (server.hasClient(client)) {
                            PlayerModel clientPlayer = client.getPlayer();
                            if (clientPlayer != null) {
                                clientPlayer.setLoading(false);
                                if (server.canStartGame()) {
                                    server.startGame();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ClientHandler
    public static void gameStartsHandler(GameStartsPacket packet) {
        if (packet != null) {
            OceanChunk oceanChunk = packet.getOceanChunk();
            if (oceanChunk != null) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if (connection instanceof ClientConnection) {
                    ClientConnection clientConnection = (ClientConnection) connection;
                    ClientPlayerModel player = clientConnection.getPlayer();
                    if (player != null) {
                        final LobbyModel lobby = player.getLobby();
                        if (lobby != null && lobby.getState().equals(GameState.IN_LOADING)) {
                            player.setOceanChunk(oceanChunk);
                            for (PlayerModel otherPlayer : lobby.getPlayersInLobby()) {
                                if (otherPlayer != player) {
                                    otherPlayer.setOceanChunk(new OceanChunk());
                                }
                            }
                            lobby.setState(GameState.IN_GAME);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    BatailleNavale.getInstance().getScreen().dispose();
                                    BatailleNavale.getInstance().setScreen(new GameScreen(lobby));
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
