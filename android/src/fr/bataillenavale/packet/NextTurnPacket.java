package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * paquet gerant la fin de tour
 */
public class NextTurnPacket extends Packet {
	/**
     * id du paquet
     */
    public final static int ID = 15;


    public NextTurnPacket() {
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
    }

    @Override
    public void deserialize(DataInputStream inputStream) {

    }
}
