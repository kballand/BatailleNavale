package fr.bataillenavale.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;



import fr.bataillenavale.game.OceanChunk;

/**
 * Classe gerant le modele d'un joueur
 */
public class PlayerModel extends NotifierModel {

	/**
     * de type string correspondant au pseudonyme du joueur
     */
    private String username;
    /**
     * booleen determinant si le joueur est pret ou non dans un salon
     */
    private boolean ready;
    /**
     * UUID du joueur
     */
    private UUID playerUUID;
    /**
     * zone dans laquelle les bateaux du joueur seront places
     */
    private OceanChunk oceanChunk;
    /**
     * booleen pour le chargement
     */
    private boolean loading;
    private boolean lost;

    /**
     * constructeur de PlayerModel
     * @param username de type String
     */
    public PlayerModel(String username) {
        this(username, false, UUID.randomUUID());
    }

    /**
     * constructeur de PlayerModel
     * @param username de type String
     * @param ready de type booleen
     * @param playerUUID de type UUID
     */
    public PlayerModel(String username, boolean ready, UUID playerUUID) {
        if (username == null)
            throw new IllegalArgumentException("Player username can't be null !");
        this.username = username;
        this.ready = ready;
        this.playerUUID = playerUUID;
        this.loading = false;
    }

    /**
     * methode de deserialisation pour l'envoie de donnees
     */
    public static PlayerModel deserialize(DataInputStream inputStream) throws IOException {
        String username = inputStream.readUTF();
        boolean ready = inputStream.readBoolean();
        UUID playerUUID = UUID.fromString(inputStream.readUTF());
        return new PlayerModel(username, ready, playerUUID);
    }

    /**
     * gere la defaite du joueur
     */
    public boolean hasLost() {
        return this.lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    /**
     * methode pour tester si le joueur est pret
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * setter de l'attribut ready
     */
    public void setReady(boolean ready) {
        if (this.ready != ready) {
            this.firePropertyChange("ready", this.ready, ready);
            this.ready = ready;
        }
    }

    /**
     * getter de l'attribut playerUUID
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * getter de l'attribut username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * setter de l'attribut username
     */
    public void setUsername(String username) {
        if (username != null && !this.username.equals(username)) {
            this.firePropertyChange("username", this.username, username);
            this.username = username;
        }
    }

    /**
     * methode permettant la serialisation pour l'envoie de donnees
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(this.username);
        outputStream.writeBoolean(this.ready);
        outputStream.writeUTF(this.playerUUID.toString());
    }

    /**
     * getter de l'attribut oceanChunk
     */
    public OceanChunk getOceanChunk() {
        return this.oceanChunk;
    }

    /**
     * setter de l'attribut oceanChunk
     */
    public void setOceanChunk(OceanChunk oceanChunk) {
        if (oceanChunk != null)
            this.oceanChunk = oceanChunk;
    }

    /**
     * regarde si le joueur est en chargement
     */
    public boolean isLoading() {
        return this.loading;
    }

    /**
     * setter de l'attribut loading
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerModel && ((PlayerModel) o).getPlayerUUID().toString().equals(this.playerUUID.toString());
    }
}
