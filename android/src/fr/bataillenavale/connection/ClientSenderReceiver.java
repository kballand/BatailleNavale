package fr.bataillenavale.connection;

import com.badlogic.gdx.Gdx;

import java.net.Socket;

import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.handler.PacketHandler;
import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.screens.MainMenuScreen;

/**
 * Gestionnaire des flux d'envois et de reception des données côté client
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class ClientSenderReceiver extends SenderReceiver {

    /**
     * Constructeur du gestionnaire de flux
     *
     * @param socket Socket sur lequel gerer le flux
     */
    public ClientSenderReceiver(Socket socket) {
        super(socket);
    }

    @Override
    public void onUnexpectedDisconnect() {
        BatailleNavale.getInstance().showText("Connection avec l'hôte perdue !", true);
        ConnectionManager.getInstance().closeConnection();
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                BatailleNavale.getInstance().getScreen().dispose();
                ConnectionManager.getInstance().closeConnection();
                BatailleNavale.getInstance().setScreen(new MainMenuScreen());
            }
        });
    }

    @Override
    public void receive(Packet received) {
        PacketHandler.getInstance().handlePacket(received);
    }
}
