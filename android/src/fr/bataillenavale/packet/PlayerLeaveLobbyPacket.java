package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'un joueur quittant le salon
 */
public class PlayerLeaveLobbyPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 8;

    /**
     * UUID du joueur
     */
    private UUID playerUUID;

    public PlayerLeaveLobbyPacket() {
    }

    /**
     * constructeur de PlayerLeaveLobbyPacket
     * @param playerUUID UUID
     */
    public PlayerLeaveLobbyPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public int getID() {
        return ID;
    }

    /**
     * getter de playerUUID
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(playerUUID.toString());
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.playerUUID = UUID.fromString(inputStream.readUTF());
    }
}
