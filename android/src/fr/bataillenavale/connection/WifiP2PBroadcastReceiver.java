package fr.bataillenavale.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Classe représentant le receveur d'informations concernant le bluetooth
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class WifiP2PBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiManager.WIFI_STATE_DISABLED) {
                if (connectionManager != null) {
                    connectionManager.turnWifiOn();
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected() && connectionManager != null) {
                ConnectionManager.getInstance().requestConnectionInfo();
            }
        }
    }
}
