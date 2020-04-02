package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.bataillenavale.models.LobbyModel;

/**
 * paquet pour la gestion d'une personne qui rejoint
 */
public class JoinLobbyInformationPacket extends Packet {
	/**
	 * id du paquet
	 */
    public final static int ID = 4;

    /**
     * salon dont on veut les informations
     */
    private LobbyModel lobby;

    public JoinLobbyInformationPacket() {
    }

    /**
     * constructeur de JoinLobbyInformationPacket
     * @param lobby LobbyModel
     */
    public JoinLobbyInformationPacket(LobbyModel lobby) {
        this.lobby = lobby;
    }

    @Override
    public int getID() {
        return ID;
    }

    /**
     * getter de lobby
     */
    public LobbyModel getLobby() {
        return this.lobby;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        this.lobby.serialize(outputStream);
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.lobby = LobbyModel.deserialize(inputStream);
    }
}
