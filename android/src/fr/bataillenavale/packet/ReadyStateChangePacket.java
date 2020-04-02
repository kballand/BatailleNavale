package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'un changement d'etat
 */
public class ReadyStateChangePacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 5;

    /**
     * UUID du joueur
     */
    private UUID playerUUID;

    public ReadyStateChangePacket() {
    }

    /**
     * constructeur de ReadyStateChangePacket
     * @param playerUUId UUID
     */
    public ReadyStateChangePacket(UUID playerUUID) {
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
