package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import fr.bataillenavale.exception.InvalidTilePositionException;
import fr.bataillenavale.game.ShipPart;

/**
 * paquet pour la gestion d'un morceau de bateau detruit
 */
public class ShipPartDestroyedPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 18;

    /**
     * UUID du joueur
     */
    private UUID playerUUID;
    /**
     * partie du bateau 
     */
    private ShipPart shipPart;

    public ShipPartDestroyedPacket() {
    }

    /**
     * constructeur de ShipPartDestroyedPacket
     * @param playerUUID UUID
     * @param shipPart ShipPart
     */
    public ShipPartDestroyedPacket(UUID playerUUID, ShipPart shipPart) {
        this.playerUUID = playerUUID;
        this.shipPart = shipPart;
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

    /**
     * getter de shipPart
     */
    public ShipPart getShipPart() {
        return this.shipPart;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(this.playerUUID.toString());
        this.shipPart.serialize(outputStream);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException, InvalidTilePositionException {
        this.playerUUID = UUID.fromString(inputStream.readUTF());
        this.shipPart = ShipPart.deserialize(inputStream);
    }
}
