package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'une expulsion d'un joueur
 */
public class KickPlayerPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 6;

    /**
     * UUID du joueur
     */
    private UUID playerUUID;

    public KickPlayerPacket() {
    }

    /**
     * constructeur de KickPlayerPacket
     * @param playerUUID UUID
     */
    public KickPlayerPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public int getID() {
        return ID;
    }

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
