package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.bataillenavale.exception.InvalidShipPositionException;
import fr.bataillenavale.exception.InvalidTilePositionException;

/**
 * classe correspondant a un paquet
 */
public abstract class Packet {

	/**
     * getter de l'attribut ID
     */
    public abstract int getID();

    /**
     * methode permettant l'envoie de donnees
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.getID());
    }

    /**
     * methode permettant la reception de donnees
     */
    public abstract void deserialize(DataInputStream inputStream) throws IOException, InvalidShipPositionException, InvalidTilePositionException;
}
