package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'un changement de pseudonyme
 */
public class PlayerUsernameChangePacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 9;

    /**
     * UUID du joueur
     */
    private UUID playerUUID;
    /**
     * nouveau pseudonyme
     */
    private String newUsername;

    public PlayerUsernameChangePacket() {
    }

    /**
     * constructeur de PlayerUsernameChangePacket
     * @param playerUUID UUID
     * @param newUsername String
     */
    public PlayerUsernameChangePacket(UUID playerUUID, String newUsername) {
        this.playerUUID = playerUUID;
        this.newUsername = newUsername;
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
     * getter de newUsername
     */
    public String getNewUsername() {
        return this.newUsername;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(playerUUID.toString());
        outputStream.writeUTF(this.newUsername);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.playerUUID = UUID.fromString(inputStream.readUTF());
        this.newUsername = inputStream.readUTF();
    }
}
