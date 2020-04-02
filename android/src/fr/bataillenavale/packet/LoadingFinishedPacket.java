package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * paquet pour la gestion d'un chargement fini
 */
public class LoadingFinishedPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 12;

    public LoadingFinishedPacket() {
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
