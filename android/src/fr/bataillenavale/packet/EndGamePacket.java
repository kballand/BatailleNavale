package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * paquet pour la gestion d'une fin de partie
 */
public class EndGamePacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 13;

    /**
     * vainqueur final
     */
    private String winner;

    public EndGamePacket() {
    }

    /**
     * constructeur de EndGamePacket
     * @param winner String
     */
    public EndGamePacket(String winner) {
        this.winner = winner;
    }

    /**
     * getter de winner
     */
    public String winner() {
        return this.winner;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(this.winner);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.winner = inputStream.readUTF();
    }
}
