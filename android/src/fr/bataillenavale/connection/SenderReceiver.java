package fr.bataillenavale.connection;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.packet.PacketManager;

/**
 * Gestionnaire des flux d'envois et de reception des données
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public abstract class SenderReceiver implements Closeable, Runnable {
    // Le socket sur lequel sont envoyées ou recus les données
    private Socket socket;
    // Le flux d'entré du socket
    private DataInputStream inputStream;
    // Le flux de sortie du socket
    private DataOutputStream outputStream;
    // Booleen indiquant si la connection a ete fermee de force ou pas
    private boolean forcedDisconnect;

    /**
     * Constructeur du gestionnaire d'envois et de reception de données
     * sur un socket
     *
     * @param socket Le socket sur lequel sont envoyées ou recus les données
     */
    public SenderReceiver(Socket socket) {
        this.socket = socket;
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ignored) {
        }
        this.forcedDisconnect = true;
    }

    @Override
    public void run() {
        while (this.socket != null && !this.socket.isClosed() && this.socket.isConnected()) {
            forcedDisconnect = false;
            try {
                Packet received = PacketManager.getInstance().getPacket(this.inputStream);
                this.receive(received);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        if (!this.forcedDisconnect) {
            this.onUnexpectedDisconnect();
        }
    }

    /**
     * Methode appelee lorsque la connection a ete ferme de maniere innatendue
     */
    public abstract void onUnexpectedDisconnect();

    /**
     * Methode appelee lors de la reception d'un packet
     *
     * @param received Packet recu
     */
    public abstract void receive(Packet received);

    /**
     * Méthode permettant d'envoyer un packet avec le socket
     *
     * @param sent Le packet envoyé
     */
    public void send(Packet sent) {
        try {
            sent.serialize(this.outputStream);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
        if (this.socket != null) {
            this.forcedDisconnect = true;
            try {
                this.socket.close();
            } catch (IOException ignored) {
            }
            this.socket = null;
        }
    }
}
