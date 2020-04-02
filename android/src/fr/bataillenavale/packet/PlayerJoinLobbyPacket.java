package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.bataillenavale.models.PlayerModel;

/**
 * paquet pour la gestion d'un nouveau joueur dans le salon
 */
public class PlayerJoinLobbyPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 7;
    /**
     * joueur qui rejoint
     */

    private PlayerModel player;

    public PlayerJoinLobbyPacket() {
    }

    /**
     * constructeur de PlayerJoinLobbyPacket
     * @param player PlayerModel
     */
    public PlayerJoinLobbyPacket(PlayerModel player) {
        this.player = player;
    }

    @Override
    public int getID() {
        return ID;
    }

    /**
     * getter de player
     */
    public PlayerModel getPlayer() {
        return this.player;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        this.player.serialize(outputStream);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.player = PlayerModel.deserialize(inputStream);
    }
}
