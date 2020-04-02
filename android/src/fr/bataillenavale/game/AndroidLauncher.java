package fr.bataillenavale.game;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.handler.PacketHandler;
import fr.bataillenavale.packet.PacketManager;

/**
 * Classe representant le lanceur de l'application
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class AndroidLauncher extends AndroidApplication {

    // Instance unique de l'application
    private static AndroidLauncher instance;

    // Bataille navale de l'application
    private BatailleNavale batailleNavale;

    /**
     * Méthode permettant de recupérer l'instance unique de l'application
     *
     * @return L'instance unique de l'application
     */
    public static AndroidLauncher getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        this.setupApplication();
        this.setupPacketsSystem();
    }

    /**
     * Mise en place de l'application et lancement du jeu
     */
    private void setupApplication() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.batailleNavale = new BatailleNavale();
        initialize(this.batailleNavale, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.batailleNavale.dispose();
        ConnectionManager.getInstance().stop();
    }

    /**
     * Mise en place du système de gestion des packets
     */
    public void setupPacketsSystem() {
        PacketManager.loadPackets();
        PacketHandler.loadPacketsHandlers();
    }
}

