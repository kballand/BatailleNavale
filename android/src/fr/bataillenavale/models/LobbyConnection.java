package fr.bataillenavale.models;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Classe gerant la connexion au salon
 */
public class LobbyConnection {
    private String lobbyName;
    private WifiP2pDevice hostDevice;

    /**
     * constructeur de LobbyConnection
     * @param lobbyName String
     * @param hostDevice WifiP2Device
     */
    public LobbyConnection(String lobbyName, WifiP2pDevice hostDevice) {
        this.lobbyName = lobbyName;
        this.hostDevice = hostDevice;
    }

    /**
     * getter de l'attribut lobbyName
     */
    public String getLobbyName() {
        return this.lobbyName;
    }

    /**
     * getter de l'attribut hostDevice
     */
    public WifiP2pDevice getHostDevice() {
        return this.hostDevice;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LobbyConnection && ((LobbyConnection) o).getHostDevice().deviceAddress.equals(this.hostDevice.deviceAddress);
    }
}
