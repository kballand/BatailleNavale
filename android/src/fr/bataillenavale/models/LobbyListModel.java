package fr.bataillenavale.models;

import java.util.HashSet;
import java.util.Set;

/**
 * classe pour le modele de la liste de salons
 */
public class LobbyListModel extends NotifierModel {
    private Set<LobbyConnection> lobbyConnections;

    /**
     * constructeur de lobbyListModel
     */
    public LobbyListModel() {
        this.lobbyConnections = new HashSet<>();
    }

    /**
     * recupere les salons disponibles
     */
    public LobbyConnection[] getAvailableLobby() {
        LobbyConnection[] availableLobby = new LobbyConnection[this.lobbyConnections.size()];
        this.lobbyConnections.toArray(availableLobby);
        return availableLobby;
    }

    /**
     * ajoute un salon a la liste
     */
    public void addAvailableLobby(LobbyConnection lobbyConnection) {
        if (lobbyConnection != null && !this.lobbyConnections.contains(lobbyConnection)) {
            LobbyConnection[] oldValue = this.getAvailableLobby();
            this.lobbyConnections.add(lobbyConnection);
            this.firePropertyChange("lobbyConnections", oldValue, this.getAvailableLobby());
        }
    }

    /**
     * supprime les slaons de la liste
     */
    public void clearAvailableLobby() {
        if (!this.lobbyConnections.isEmpty()) {
            LobbyConnection[] oldValue = this.getAvailableLobby();
            this.lobbyConnections.clear();
            this.firePropertyChange("lobbyConnections", oldValue, this.getAvailableLobby());
        }
    }
}
