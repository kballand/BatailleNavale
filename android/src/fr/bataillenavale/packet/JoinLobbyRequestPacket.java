package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * paquet pour la gestion d'une requete pour rejoindre un salon
 */
public class JoinLobbyRequestPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 2;

    /**
     * nom du joueur qui veut rejoindre
     */
    private String username;
    /**
     * UUID du joueur
     */
    private UUID playerUUID;

    public JoinLobbyRequestPacket() {
    }

    /**
     * constructeur de JoinLobbyRequestPacket
     * @param username String
     * @param playerUUID UUID
     */
    public JoinLobbyRequestPacket(String username, UUID playerUUID) {
        this.username = username;
        this.playerUUID = playerUUID;
    }

    @Override
    public int getID() {
        return ID;
    }
    
    /**
     * getter de username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * getter de playerUUID
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(this.username);
        outputStream.writeUTF(this.playerUUID.toString());
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.username = inputStream.readUTF();
        this.playerUUID = UUID.fromString(inputStream.readUTF());
    }
}
