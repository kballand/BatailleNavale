package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'un chat textuel
 */
public class ChatMessagePacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 1;

    /**
     * UUID du joueur
     */
    private UUID playerUUID;
    /**
     * message envoye
     */
    private String message;

    public ChatMessagePacket() {
    }

    /**
     * constructeur de ChatMessagePacket
     * @param playerUUID UUID
     * @param message String
     */
    public ChatMessagePacket(UUID playerUUID, String message) {
        this.playerUUID = playerUUID;
        this.message = message;
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
     * getter de message
     */
    public String getMessage() {
        return this.message;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(this.playerUUID.toString());
        outputStream.writeUTF(this.message);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.playerUUID = UUID.fromString(inputStream.readUTF());
        this.message = inputStream.readUTF();
    }
}
