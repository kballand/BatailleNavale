package fr.bataillenavale.connection;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.lang.reflect.Method;

import fr.bataillenavale.game.AndroidLauncher;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.models.LobbyConnection;
import fr.bataillenavale.models.LobbyListModel;
import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.screens.LobbyScreen;
import fr.bataillenavale.utils.GameConstants;

/**
 * Classe correspondant au gestionnaire de connection
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class ConnectionManager implements WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener {
    // Instance unique du gestionnaire
    private static ConnectionManager instance;

    // Contexte depuis lequel le gestionnaire de connections est lance
    private Context context;
    // Gestionnaire du wifi
    private WifiManager wifiManager;
    // Gestionnaire de la connection peer-to-peer
    private WifiP2pManager p2pManager;
    // Channel de la connection peer-to-peer
    private Channel channel;
    // Filtre des evenements a gerer pour le receiver
    private IntentFilter intentFilter;
    // Receveur des evenements du filtre
    private WifiP2PBroadcastReceiver receiver;
    // Liste des salons disponibles
    private LobbyListModel lobbyList;
    // Nombre max de clients defini lors de creation du salon
    private int definedMaxClients;
    // Nom du salon cree
    private String lobbyName;
    // Connection actuelle
    private Connection connection;

    /**
     * Constructeur du gestionnaire de connections
     */
    private ConnectionManager(Context context) {
        this.context = context;
        this.requestPermissions();
        this.setupWifi();
        this.setupWifiP2P();
        this.cleanUp();
        this.setupServiceListener();
        this.setupIntentFilter();
        this.setupReceiver();
        this.startReceiver();
        this.lobbyList = new LobbyListModel();
        this.lobbyName = "";
    }

    /**
     * Méthode permettant de mettre en place le gestionnaire de connections
     */
    public static synchronized void setupConnection() {
        if (instance == null) {
            instance = new ConnectionManager(AndroidLauncher.getInstance());
        }
    }

    /**
     * Getter de l'instance unique du gestionnaire de connection
     *
     * @return L'instance unique du gestionnaire de connection
     */
    public static ConnectionManager getInstance() {
        return instance;
    }

    /**
     * Permet de demander les permissions necessaire pour l'utilisation de l'AndroidLauncher.getInstance().
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && AndroidLauncher.getInstance().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AndroidLauncher.getInstance().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    /**
     * Methode permettant d'initialiser le wifi
     */
    private void setupWifi() {
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        if (this.wifiManager == null) {
            BatailleNavale.getInstance().showText("Votre téléphone ne supporte pas le wifi !", true);
            Gdx.app.exit();
            System.exit(0);
        }
        this.turnWifiOn();
    }

    /**
     * Methode permettant de lancer le wifi
     */
    public void turnWifiOn() {
        if (!this.wifiManager.isWifiEnabled()) {
            this.wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * Methode permettant d'initialiser le wifi peer-to-peer
     */
    private void setupWifiP2P() {
        AndroidLauncher app = AndroidLauncher.getInstance();
        this.p2pManager = (WifiP2pManager) app.getSystemService(Context.WIFI_P2P_SERVICE);
        if (p2pManager == null) {
            BatailleNavale.getInstance().showText("Votre téléphone ne supporte pas le wifi P2P !", true);
            Gdx.app.exit();
            System.exit(0);
        }
        this.channel = this.p2pManager.initialize(this.context, this.context.getMainLooper(), this);
    }

    /**
     * Permet de mettre en place le filtre des changements a traiter sur le bluetooth
     */
    private void setupIntentFilter() {
        this.intentFilter = new IntentFilter();
        this.intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    }

    /**
     * Méthode permettant de créer un nouveau receveur d'informations concernant le bluetooth
     */
    private void setupReceiver() {
        this.receiver = new WifiP2PBroadcastReceiver();
    }

    /**
     * Méthode permettant de lancer la reception d'informations concernant le bluetooth
     */
    public void startReceiver() {
        this.context.registerReceiver(this.receiver, this.intentFilter);
    }

    /**
     * Méthode permettant de stopper la reception d'informations concernant le bluetooth
     */
    public void stopReceiver() {
        try {
            this.context.unregisterReceiver(this.receiver);
        } catch (Exception ignored) {
        }
    }

    /**
     * Methode permettant de nettoyer le connection
     */
    private void cleanUp() {
        this.closeConnection();
        this.stopLobbyDiscovery();
        this.deletePersistentGroups();
    }

    /**
     * Methode permettant de supprimer les groupes persistents du wifi peer-to-peer
     */
    private void deletePersistentGroups() {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals("deletePersistentGroup")) {
                    for (int netid = 0; netid < 32; netid++) {
                        method.invoke(this.p2pManager, this.channel, netid, null);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Methode permettant d'initilialiser l'ecoute de service
     */
    private void setupServiceListener() {
        WifiP2pManager.DnsSdServiceResponseListener serviceListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                if (registrationType.startsWith(GameConstants.SERVICE_NAME) && instanceName != null && !instanceName.trim().isEmpty()) {
                    ConnectionManager.this.lobbyList.addAvailableLobby(new LobbyConnection(instanceName, srcDevice));
                }
            }
        };
        this.p2pManager.setDnsSdResponseListeners(this.channel, serviceListener, null);
    }

    /**
     * Méthode permettant de lancer la recherche de salons à proximité
     */
    public void startLobbyDiscovery() {
        WifiP2pDnsSdServiceRequest request = WifiP2pDnsSdServiceRequest.newInstance(GameConstants.SERVICE_NAME);
        this.p2pManager.addServiceRequest(this.channel, request, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConnectionManager.this.p2pManager.discoverServices(ConnectionManager.this.channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        BatailleNavale.getInstance().showText("Recherche de salons en cours !", false);
                    }

                    @Override
                    public void onFailure(int reason) {
                        BatailleNavale.getInstance().showText("Echec de la recherche de salons !", false);
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                BatailleNavale.getInstance().showText("Echec de la recherche de salons !", false);
            }
        });
        this.lobbyList.clearAvailableLobby();
    }

    /**
     * Arrête la recherche de salons à proximité
     */
    public void stopLobbyDiscovery() {
        this.p2pManager.clearServiceRequests(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
        this.p2pManager.stopPeerDiscovery(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    /**
     * Début de l'administration d'un salon
     *
     * @param lobbyName Le nom du salon
     */
    public void startHosting(String lobbyName, int maxClients) {
        this.lobbyName = lobbyName;
        this.definedMaxClients = maxClients;
        this.p2pManager.createGroup(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                try {
                    ConnectionManager.this.startLocalService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int reason) {
                BatailleNavale.getInstance().showText("Echec de la création du salon !", false);
            }
        });
    }

    /**
     * Methode permettant de supprimer le groupe cree avec le wifi peer-to-peer
     */
    public void removeGroup() {
        this.p2pManager.removeGroup(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    /**
     * Methode permettant d'arreter un service local
     */
    public void stopLocalServices() {
        this.p2pManager.clearLocalServices(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    /**
     * Methode permettant de stopper toutes les activites du gestionnaire de connection
     */
    public void stop() {
        this.stopReceiver();
        this.stopLobbyDiscovery();
        this.closeConnection();
        this.deletePersistentGroups();
    }

    /**
     * Methode permettant de lancer la requete pour obtenir les informations de la connection
     */
    public void requestConnectionInfo() {
        this.p2pManager.requestConnectionInfo(this.channel, this);
    }

    /**
     * Méthode permettant de créer un serveur pour administrer son salon
     *
     * @throws IOException En cas d'erreur lors de la création du serveur
     */
    private void host() throws IOException {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (IOException ignored) {
            }
        }
        this.connection = new ServerConnection(this.lobbyName, this.definedMaxClients, GameConstants.LISTENING_PORT);
        this.connection.start();
    }

    /**
     * Methode permettant de lancer un service local
     *
     * @throws IOException En cas d'echec de l'hosting du salon
     */
    private void startLocalService() throws IOException {
        this.host();
        String instanceName = this.lobbyName;
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(instanceName, GameConstants.SERVICE_NAME, null);
        this.p2pManager.addLocalService(this.channel, service, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        BatailleNavale.getInstance().getScreen().dispose();
                        BatailleNavale.getInstance().setScreen(new LobbyScreen("En attente d'autres joueurs...", ConnectionManager.this.connection.getPlayer().getLobby(), true));
                    }
                });
            }

            public void onFailure(int reason) {
                ConnectionManager.this.closeConnection();
                BatailleNavale.getInstance().showText("Echec de la création du salon !", false);
            }
        });
    }

    /**
     * Méthode permettant d'envoyer un packet sur la connection
     *
     * @param packet Packet à envoyer
     */
    public void sendPacket(Packet packet) {
        if (this.connection != null) {
            this.connection.sendPacket(packet);
        }
    }

    /**
     * Getter permettant de récuperer la liste des salons à proximité
     *
     * @return La liste des salons à proximité
     */
    public LobbyListModel getLobbyList() {
        return this.lobbyList;
    }

    /**
     * Methode permettant de se connecter a un salon
     *
     * @param lobbyConnection Salon auquel se connecter
     */
    public void connectTo(LobbyConnection lobbyConnection) {
        this.closeConnection();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = lobbyConnection.getHostDevice().deviceAddress;
        config.groupOwnerIntent = 0;
        this.p2pManager.connect(this.channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                BatailleNavale.getInstance().showText("Echec de la connection avec l'hôte !", false);
            }
        });
    }

    /**
     * Methode permettant de fermer la connection actuelle
     */
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (IOException ignored) {
            }
            this.connection = null;
        }
        this.removeGroup();
        this.stopLocalServices();
        this.p2pManager.cancelConnect(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    /**
     * Methode permettant de recuperer la connection actuelle
     *
     * @return La connection actuelle
     */
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.groupFormed && !info.isGroupOwner) {
            if (this.connection != null) {
                try {
                    this.connection.close();
                } catch (IOException ignored) {
                }
            }
            this.connection = new ClientConnection(info.groupOwnerAddress.getHostAddress(), GameConstants.LISTENING_PORT);
            this.connection.start();
        }
    }
}
