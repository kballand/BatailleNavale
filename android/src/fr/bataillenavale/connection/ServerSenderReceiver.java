package fr.bataillenavale.connection;

import java.net.Socket;

import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.handler.PacketHandler;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.packet.PlayerLeaveLobbyPacket;

/**
 * Gestionnaire des flux d'envois et de reception des données côté serveur
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class ServerSenderReceiver extends SenderReceiver {
    // Joueur du client
    private PlayerModel player;

    /**
     * Constructeur du gestionnaire
     *
     * @param socket Socket pour communiquer avec le client
     */
    public ServerSenderReceiver(Socket socket) {
        super(socket);
    }

    @Override
    public void onUnexpectedDisconnect() {
        Connection connection = ConnectionManager.getInstance().getConnection();
        if (connection instanceof ServerConnection) {
            ServerConnection serverConnection = (ServerConnection) connection;
            if (serverConnection.hasClient(this)) {
                if (player != null) {
                    BatailleNavale.getInstance().showText(player.getUsername() + " a quitté le salon !", true);
                    serverConnection.getPlayer().getLobby().removePlayer(player);
                    serverConnection.sendPacket(new PlayerLeaveLobbyPacket(player.getPlayerUUID()));
                }
                serverConnection.removeClient(this);
            }
        }
    }

    @Override
    public void receive(Packet received) {
        PacketHandler.getInstance().handlePacket(received, this);
    }

    /**
     * Methode permettant de recuperer le joueur du client
     *
     * @return Le joueur du client
     */
    public PlayerModel getPlayer() {
        return this.player;
    }

    /**
     * Methode permettant de definir le joueur du client
     *
     * @param player Le joueur du client
     */
    public void initPlayer(PlayerModel player) {
        if (this.player == null && player != null) {
            this.player = player;
        }
    }
}
