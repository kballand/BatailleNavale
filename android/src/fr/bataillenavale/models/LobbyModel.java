package fr.bataillenavale.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.bataillenavale.animation.WaterTile;
import fr.bataillenavale.game.GameState;
import fr.bataillenavale.game.OceanChunk;

/**
 * Classe pour le modele du salon
 */
public class LobbyModel extends NotifierModel {
	/**
     * String nom du salon
     */
    private String lobbyName;
    /**
     * int taille du salon
     */
    private int maxPlayers;
    /**
     * List liste des joueurs dans le salon
     */
    private List<PlayerModel> playersInLobby;
    /**
     * GameState etat de la partie
     */
    private GameState state;
    /**
     * int prochain joueur qui joue
     */
    private int nextPlayerTurn;
    /**
     * PlayerModel joueur en train de jouer
     */
    private PlayerModel playerTurn;

    /**
     * constructeur de LobbyModel
     * @param lobbyName String
     * @param maxPlayers int
     */
    public LobbyModel(String lobbyName, int maxPlayers) {
        this(lobbyName, maxPlayers, new ArrayList<PlayerModel>());
    }

    /**
     * constructeur de LobbyModel
     * @param lobbyName String
     * @param maxPlayers int
     * @param playersInLobby List<PlayerModel>
     */
    public LobbyModel(String lobbyName, int maxPlayers, List<PlayerModel> playersInLobby) {
        if (lobbyName == null)
            throw new IllegalArgumentException("Lobby name can't be null !");
        if (maxPlayers <= 0)
            throw new IllegalArgumentException("Max players must be strictly positivite !");
        if (playersInLobby == null)
            throw new IllegalArgumentException("Players in lobby can't be null !");
        if (playersInLobby.size() > maxPlayers)
            throw new IllegalArgumentException("Too much players in the lobby !");
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.playersInLobby = playersInLobby;
        this.state = GameState.IN_LOBBY;
    }

    /**
     * methode de deserialisation permettant la reception de donnees
     */
    public static LobbyModel deserialize(DataInputStream inputStream) throws IOException {
        String lobbyName = inputStream.readUTF();
        int maxPlayers = inputStream.readInt();
        int nbPlayers = inputStream.readInt();
        List<PlayerModel> playersInLobby = new ArrayList<>(nbPlayers);
        for (int i = 0; i < nbPlayers; i++) {
            playersInLobby.add(PlayerModel.deserialize(inputStream));
        }
        return new LobbyModel(lobbyName, maxPlayers, playersInLobby);
    }

    /**
     * getter de l'attribut PlayerInLobby
     */
    public PlayerModel[] getPlayersInLobby() {
        PlayerModel[] players = new PlayerModel[playersInLobby.size()];
        this.playersInLobby.toArray(players);
        return players;
    }

    /**
     * Permet l'ajout d'un joueur dans la liste des joueurs du salon
     */
    public void addPlayer(PlayerModel player) {
        if (player != null && !this.playersInLobby.contains(player) && this.playersInLobby.size() < maxPlayers) {
            PlayerModel[] oldValue = this.getPlayersInLobby();
            this.playersInLobby.add(player);
            this.firePropertyChange("playersInLobby", oldValue, this.getPlayersInLobby());
        }
    }

    /**
     * getter de l'attribut lobbyName
     */
    public String getLobbyName() {
        return this.lobbyName;
    }

    /**
     * permet de voir si la partie est finie ou non
     */
    public boolean gameFinished() {
        int loosers = 0;
        for (PlayerModel p : playersInLobby) {
            if (p.hasLost()) {
                loosers++;
            }
        }
        return (loosers == playersInLobby.size() - 1);
    }

    /**
     * methode permettant d'avoir le nom du vainqueur
     */
    public String winner() {
        String winner = "NoName";
        for (PlayerModel p : playersInLobby) {
            if (!p.hasLost()) {
                winner = p.getUsername();
            }
        }
        return winner;
    }

    /**
     * methode permettant d'avoir la liste des joueurs restants
     */
    public PlayerModel[] getRemainingPlayers() {
        List<PlayerModel> lRemaining = new ArrayList<>();
        for (PlayerModel playerModel : this.playersInLobby) {
            if (!playerModel.hasLost()) {
                lRemaining.add(playerModel);
            }
        }
        PlayerModel[] tRemaining = new PlayerModel[lRemaining.size()];
        lRemaining.toArray(tRemaining);
        return tRemaining;
    }

    /**
     * Permet d'obtenir le nombre de chunks en fonction des joueurs restant
     */
    public int getChunkNumber(PlayerModel playerModel) {
        if (playerModel != null) {
            PlayerModel[] remainingPlayers = this.getRemainingPlayers();
            for (int i = 0; i < remainingPlayers.length; i++) {
                PlayerModel player = remainingPlayers[i];
                if (player.equals(playerModel)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * supprimer un joueur de la liste
     */
    public void removePlayer(PlayerModel player) {
        int index = this.playersInLobby.indexOf(player);
        if (player != null && index >= 0) {
            if (index < this.nextPlayerTurn)
                --this.nextPlayerTurn;
            PlayerModel[] oldValue = this.getPlayersInLobby();
            this.playersInLobby.remove(player);
            this.firePropertyChange("playersInLobby", oldValue, this.getPlayersInLobby());
        }
    }

    /**
     * permet de savoir si la liste est vide
     */
    public boolean hasPlayer(PlayerModel player) {
        if (player == null)
            return false;
        return this.playersInLobby.contains(player);
    }

    /**
     * methode permettant l'envoie de donnees
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(this.lobbyName);
        outputStream.writeInt(this.maxPlayers);
        outputStream.writeInt(this.playersInLobby.size());
        for (PlayerModel player : this.playersInLobby) {
            player.serialize(outputStream);
        }
    }

    /**
     * getter de l'attribut maxPlayers
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * permet de savoir si le salon est plein
     */
    public boolean isFull() {
        return this.playersInLobby.size() >= this.maxPlayers;
    }

    /**
     * Obtient la valeur du playerUUID
     */
    public PlayerModel getPlayerByUUID(UUID playerUUID) {
        String stringUUID = playerUUID.toString();
        for (PlayerModel player : this.playersInLobby) {
            if (stringUUID.equals(player.getPlayerUUID().toString())) {
                return player;
            }
        }
        return null;
    }

    /**
     * getter de l'attribut state
     */
    public GameState getState() {
        return this.state;
    }

    /**
     * setter de l'attribut state
     */
    public void setState(GameState state) {
        if (state != null)
            this.state = state;
    }

    /**
     * passe au tour suivant
     */
    public void nextTurn() {
        if (this.nextPlayerTurn >= this.playersInLobby.size())
            this.nextPlayerTurn = 0;
        this.playerTurn = this.playersInLobby.get(this.nextPlayerTurn++);
    }

    /**
     * getter de l'attribut playerTurn
     */
    public PlayerModel getPlayerTurn() {
        return this.playerTurn;
    }

    /**
     * remet a zero l'attribut playerTurn
     */
    public void resetPlayerTurn() {
        this.playerTurn = null;
    }

    /**
     * getter de l'attribut waterTile de chaque joueur
     */
    public PlayerModel getTileOwner(WaterTile waterTile) {
        for (PlayerModel player : this.playersInLobby) {
            OceanChunk oceanChunk = player.getOceanChunk();
            if (oceanChunk != null && oceanChunk.containsWaterTile(waterTile)) {
                return player;
            }
        }
        return null;
    }
}
