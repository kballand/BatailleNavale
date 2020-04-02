package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.bataillenavale.exception.InvalidShipPositionException;
import fr.bataillenavale.game.OceanChunk;

/**
 * paquet pour la gestion d'un debut de partie
 */
public class GameStartsPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 10;

    /**
     * zone de l'ocean 
     */
    private OceanChunk oceanChunk;

    public GameStartsPacket() {
    }

    /**
     * constructeur de GameStartsPacket
     * @param oceanChunk OceanChunk
     */
    public GameStartsPacket(OceanChunk oceanChunk) {
        this.oceanChunk = oceanChunk;
    }

    @Override
    public int getID() {
        return ID;
    }

    /**
     * getter de oceanChunk
     */
    public OceanChunk getOceanChunk() {
        return this.oceanChunk;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        oceanChunk.serialize(outputStream);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException, InvalidShipPositionException {
        this.oceanChunk = OceanChunk.deserialize(inputStream);
    }
}
