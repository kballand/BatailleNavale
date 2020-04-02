package fr.bataillenavale.models;

import fr.bataillenavale.animation.WaterTile;

/**
 * Classe qui gere le modele d'un joueur
 */
public class ClientPlayerModel extends PlayerModel {
	/**
     * Attribut privee correspondant au lobby
     */
    private LobbyModel lobby;
    /**
     * Attribut de type WaterTile correspondant a une case d'eau
     */
    private WaterTile selectedTile;

    /**
     * Constructeur de clientPlayerModel
     * @param username de type String
     */
    public ClientPlayerModel(String username) {
        super(username);
    }

    /**
     * getter de l'attribut lobby
     */
    public LobbyModel getLobby() {
        return this.lobby;
    }

    /**
     * setter de l'attribut lobby
     */
    public void setLobby(LobbyModel lobby) {
        this.lobby = lobby;
    }

    /**
     * setter de l'attribut selectedTile
     */
    public void setSelectedTile(WaterTile selectedTile) {
        this.selectedTile = selectedTile;
    }

    /**
     * getter de l'attribut selectedTile
     */
    public WaterTile getSelectedTile() {
        return this.selectedTile;
    }
}
