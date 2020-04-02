package fr.bataillenavale.connection;

import com.badlogic.gdx.Gdx;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.GameState;
import fr.bataillenavale.game.OceanChunk;
import fr.bataillenavale.handler.PacketHandler;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.GameStartsPacket;
import fr.bataillenavale.packet.JoinLobbyRequestPacket;
import fr.bataillenavale.packet.NextTurnPacket;
import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.packet.PacketManager;
import fr.bataillenavale.screens.GameScreen;
import fr.bataillenavale.task.DelayedTask;

/**
 * Classe correspondant à une connection côté serveur
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class ServerConnection extends Connection {

    private ServerSocket serverSocket;
    private List<ServerSenderReceiver> clients;

    /**
     * Constructeur de la connection du côté serveur
     *
     * @throws IOException En cas d'échec de la création du serveur
     */
    public ServerConnection(String lobbyName, int maxClients, int listeningPort) throws IOException {
        super();
        this.serverSocket = new ServerSocket(listeningPort);
        this.clients = new ArrayList<>();
        LobbyModel lobby = new LobbyModel(lobbyName, maxClients);
        lobby.addPlayer(this.getPlayer());
        this.getPlayer().setLobby(lobby);
    }

    @Override
    public void run() {
        while (this.serverSocket != null && !this.serverSocket.isClosed()) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                if (clientSocket != null) {
                    DataInputStream customInputStream = new DataInputStream(clientSocket.getInputStream());
                    try {
                        Packet packet = PacketManager.getInstance().getPacket(customInputStream);
                        if (packet instanceof JoinLobbyRequestPacket) {
                            PacketHandler.getInstance().handlePacket(packet, new ServerSenderReceiver(clientSocket));
                        } else {
                            clientSocket.close();
                        }
                    } catch (Exception e) {
                        clientSocket.close();
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        for (ServerSenderReceiver client : this.clients) {
            client.send(packet);
        }
    }

    @Override
    public void close() throws IOException {
        for (ServerSenderReceiver client : this.clients) {
            if (client != null) {
                client.close();
            }
        }
        this.clients.clear();
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            this.serverSocket.close();
            this.serverSocket = null;
        }
    }

    /**
     * Methode permettant de savoir si le serveur a deja un client ou non
     *
     * @param client Le client
     * @return Vrai si le serveur a deja ce client, faux sinon
     */
    public boolean hasClient(ServerSenderReceiver client) {
        if (client == null)
            return false;
        return this.clients.contains(client);
    }

    /**
     * Methode permettant d'ajouter un client au serveur
     *
     * @param client Client a ajouter
     */
    public void addClient(ServerSenderReceiver client) {
        if (client != null && !this.hasClient(client)) {
            new Thread(client).start();
            this.clients.add(client);
        }
    }

    /**
     * Methode permettant de supprimer un client du serveur
     *
     * @param client Client a supprimer
     */
    public void removeClient(ServerSenderReceiver client) {
        if (client != null && this.hasClient(client)) {
            this.clients.remove(client);
            client.close();
        }
    }

    /**
     * Methode permettant de recuperer les clients du serveur
     *
     * @return Clients du serveur
     */
    public ServerSenderReceiver[] getClients() {
        ServerSenderReceiver[] clients = new ServerSenderReceiver[this.clients.size()];
        this.clients.toArray(clients);
        return clients;
    }

    /**
     * Methode permettant de recuperer un client par son UUID
     *
     * @param uuid UUID du client rechercher
     * @return Le client du serveur, ou null si il n'est pas present
     */
    public ServerSenderReceiver getClientByUUID(UUID uuid) {
        if (uuid != null) {
            for (ServerSenderReceiver client : this.clients) {
                PlayerModel clientPlayer = client.getPlayer();
                if (clientPlayer != null) {
                    UUID clientUUID = clientPlayer.getPlayerUUID();
                    if (clientUUID != null && clientUUID.toString().equals(uuid.toString())) {
                        return client;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Methode permettant de lancer la partie si les conditions sont respectees
     */
    public void startGame() {
        if (canStartGame()) {
            final LobbyModel lobby = this.getPlayer().getLobby();
            lobby.setState(GameState.IN_GAME);
            for (ServerSenderReceiver client : this.clients) {
                OceanChunk oceanChunk = new OceanChunk();
                oceanChunk.generateShips();
                client.getPlayer().setOceanChunk(oceanChunk);
                client.send(new GameStartsPacket(oceanChunk));
            }
            new DelayedTask(2000) {
                @Override
                public void run() {
                    lobby.nextTurn();
                    PlayerModel playerTurn = lobby.getPlayerTurn();
                    if (playerTurn.equals(getPlayer())) {
                        BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                    } else {
                        BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                    }
                    for (ServerSenderReceiver client : clients) {
                        client.send(new NextTurnPacket());
                    }
                }
            }.start();
            OceanChunk oceanChunk = new OceanChunk();
            oceanChunk.generateShips();
            this.getPlayer().setOceanChunk(oceanChunk);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    BatailleNavale.getInstance().getScreen().dispose();
                    BatailleNavale.getInstance().setScreen(new GameScreen(lobby));
                }
            });
        }
    }

    /**
     * Methode permettant de savoir si les conditions sont respectees pour lancer la partie
     *
     * @return Vrai si les conditions de lancement de partie sont respectees, faux sinon
     */
    public boolean canStartGame() {
        if (!this.getPlayer().getLobby().getState().equals(GameState.IN_LOADING))
            return false;
        if (this.getPlayer().isLoading())
            return false;
        for (ServerSenderReceiver client : this.clients) {
            PlayerModel player = client.getPlayer();
            if (player == null || player.isLoading())
                return false;
        }
        return true;
    }
}
