package fr.bataillenavale.connection;

import java.io.Closeable;

import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.models.ClientPlayerModel;
import fr.bataillenavale.packet.Packet;

/**
 * Classe abstraite représentant une connection
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public abstract class Connection extends Thread implements Closeable {

    // Model representant le joueur a qui appartient la connection
    private ClientPlayerModel player;

    /**
     * Constructeur d'une connection
     */
    public Connection() {
        this.player = new ClientPlayerModel(BatailleNavale.getInstance().getUsername());
    }

    /**
     * Méthode permettant d'envoyer un packet
     *
     * @param packet Le packet à envoyer
     */
    public abstract void sendPacket(Packet packet);

    /**
     * Joueur du client
     *
     * @return Le joueur du client
     */
    public ClientPlayerModel getPlayer() {
        return this.player;
    }
}
