package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'un tir dans une zone
 */
public class WaterTileDestroyedPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 17;

    /**
     * UUID du joueur dont la zone a ete ciblee
     */
    private UUID playerUUID;
    /**
     * coordonnee en x
     */
    private int x;
    /**
     * coordonnee en y
     */
    private int y;

    public WaterTileDestroyedPacket() {
    }

    /**
     * constructeur de WaterTileDestroyedPacket
     * @param playerUUID UUID
     * @param x int
     * @param y int
     */
    public WaterTileDestroyedPacket(UUID playerUUID, int x, int y) {
        this.playerUUID = playerUUID;
        this.x = x;
        this.y = y;
    }

    @Override
    public int getID() {
        return ID;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(this.playerUUID.toString());
        outputStream.writeInt(this.x);
        outputStream.writeInt(this.y);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.playerUUID = UUID.fromString(inputStream.readUTF());
        this.x = inputStream.readInt();
        this.y = inputStream.readInt();
    }
}
