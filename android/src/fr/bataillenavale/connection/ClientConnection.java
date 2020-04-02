package fr.bataillenavale.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.handler.PacketHandler;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.JoinLobbyInformationPacket;
import fr.bataillenavale.packet.JoinLobbyRequestPacket;
import fr.bataillenavale.packet.LobbyFullPacket;
import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.packet.PacketManager;

/**
 * Classe représentant une connection du côté client
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class ClientConnection extends Connection {
    // Adresse de l'hote
    private String hostAddress;
    // Port d'ecoute de l'hote
    private int listeningPort;
    // Socket de connection a l'hote
    private Socket clientSocket;
    // Gestionnaire des flux d'envois et de reception des données
    private SenderReceiver server;

    /**
     * Constructeur d'une connection côté client
     */
    public ClientConnection(String hostAddress, int listeningPort) {
        super();
        this.hostAddress = hostAddress;
        this.listeningPort = listeningPort;
    }

    @Override
    public void run() {
        this.clientSocket = new Socket();
        try {
            this.clientSocket.bind(null);
            this.clientSocket.connect(new InetSocketAddress(this.hostAddress, this.listeningPort), 500);
            PlayerModel player = this.getPlayer();
            DataOutputStream customOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());
            JoinLobbyRequestPacket requestPacket = new JoinLobbyRequestPacket(player.getUsername(), player.getPlayerUUID());
            requestPacket.serialize(customOutputStream);
            DataInputStream customInputStream = new DataInputStream(this.clientSocket.getInputStream());
            Packet received = PacketManager.getInstance().getPacket(customInputStream);
            if (received instanceof JoinLobbyInformationPacket) {
                PacketHandler.getInstance().handlePacket(received);
                this.server = new ClientSenderReceiver(this.clientSocket);
                this.server.run();
            } else {
                if (received instanceof LobbyFullPacket) {
                    PacketHandler.getInstance().handlePacket(received);
                } else {
                    BatailleNavale.getInstance().showText("Echec de la communication avec le serveur !", false);
                    ConnectionManager.getInstance().closeConnection();
                }
            }
        } catch (SocketTimeoutException e) {
            ConnectionManager.getInstance().closeConnection();
            BatailleNavale.getInstance().showText("Délai de connection à l'hôte trop long.", false);
        } catch (Exception e) {
            ConnectionManager.getInstance().closeConnection();
            e.printStackTrace();
            BatailleNavale.getInstance().showText("Echec de la connection au salon !", false);
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.server != null) {
            this.server.send(packet);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.server != null) {
            this.server.close();
            this.server = null;
        }
        if (this.clientSocket != null && !this.clientSocket.isClosed()) {
            this.clientSocket.close();
            this.clientSocket = null;
        }
    }
}
