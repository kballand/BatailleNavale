package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;

import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.connection.ServerSenderReceiver;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.GameState;
import fr.bataillenavale.models.ClientPlayerModel;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.ChatMessagePacket;
import fr.bataillenavale.packet.KickPlayerPacket;
import fr.bataillenavale.packet.LoadingStartsPacket;
import fr.bataillenavale.packet.ReadyStateChangePacket;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe d'affichage pour le salon
 */
public class LobbyScreen extends NotifiableScreen {

	/**
	 * booleen pour savoir si nous sommes l'hote
	 */
    private boolean host;
    /**
     * modele du salon
     */
    private LobbyModel lobbyModel;
    /**
     * scene du salon
     */
    private Stage stage;
    /**
     * table
     */
    private Table table;
    /**
     * texture du salon
     */
    private Skin skin;
    /**
     * status des joueurs
     */
    private TextField[] playersStatus;
    /**
     * bouton pour expulser
     */
    private TextButton kickButtons[];
    /**
     * titre du salon
     */
    private Label title;
    /**
     * zone pour ecrire
     */
    private TextField messageWriter;
    /**
     * bouton pour envoyer
     */
    private ImageButton sendButton;
    /**
     * zone de chat
     */
    private Label chat;
    /**
     * permet de voyager de haut en bas
     */
    private ScrollPane scroller;
    /**
     * bouton pour les options
     */
    private ImageButton optionsMenu;
    /**
     * bouton de retour
     */
    private TextButton returnHome;
    /**
     * bouton pour se mettre pret
     */
    private TextButton readyButton;

    /**
     * constructeur de LobbyScreen
     * @param text String
     * @param lobbyModel LobbyModel
     */
    public LobbyScreen(String text, LobbyModel lobbyModel) {
        this(text, lobbyModel, false);
    }

    /**
     * constructeur de LobbyScreen
     * @param text String
     * @param lobbyModel LobbyModel
     * @param host
     */
    public LobbyScreen(String text, LobbyModel lobbyModel, final boolean host) {
        super();
        this.lobbyModel = lobbyModel;
        this.host = host;
        this.skin = GameUtils.getDefaultSkin();
        this.stage = new Stage(BatailleNavale.getInstance().getViewport());
        this.table = new Table();
        this.table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.table.align(Align.center | Align.top);
        //pour adapter la police a la taille de l'ecran
//        GameUtils.setFontSize(skin.getFont("title"), GameConstants.SCREEN_HEIGHT);
        this.stage.addActor(this.table);
        this.optionsMenu = GameUtils.getImageButton("options.png", false);
        this.optionsMenu.setSize((75 * GameConstants.SCREEN_HEIGHT) / 1920F, (75 * GameConstants.SCREEN_HEIGHT) / 1920F);
        this.optionsMenu.setPosition(GameConstants.SCREEN_WIDTH - (75 * GameConstants.SCREEN_HEIGHT) / 1920F, GameConstants.SCREEN_HEIGHT - (75 * GameConstants.SCREEN_HEIGHT) / 1920F);
        this.optionsMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                BatailleNavale.getInstance().setScreen(new OptionsScreen(LobbyScreen.this));
            }
        });
        this.title = new Label(lobbyModel.getLobbyName(), new Label.LabelStyle(skin.getFont("title"), Color.WHITE));
//        this.title.setColor(Color.BLACK);
        this.title.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.table.add(title);
        this.table.row();
        final int maxPlayers = this.lobbyModel.getMaxPlayers();
        this.playersStatus = new TextField[maxPlayers];
        this.kickButtons = new TextButton[maxPlayers - 1];
        for (int i = 0; i < maxPlayers; i++) {
            TextField playerStatus = new TextField("   ", skin);
            playerStatus.setDisabled(true);
            playerStatus.setAlignment(Align.left);
            if (this.host) {
                if (i == 0) {
                    this.table.add(playerStatus).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.065f);
                } else {
                    this.table.add(playerStatus).size(GameConstants.SCREEN_WIDTH * 0.4f, GameConstants.SCREEN_HEIGHT * 0.065f).padTop(GameConstants.SCREEN_HEIGHT * 0.005f);
                    TextButton kickButton = new TextButton("Exclure", skin);
                    this.table.add(kickButton).size(GameConstants.SCREEN_WIDTH * 0.25f, GameConstants.SCREEN_HEIGHT * 0.055f).padLeft(GameConstants.SCREEN_WIDTH * 0.0005f);
                    this.kickButtons[i - 1] = kickButton;
                }
            } else {
                this.table.add(playerStatus).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.065f).padTop(GameConstants.SCREEN_HEIGHT * 0.0055f);
            }
            this.table.row();
            this.playersStatus[i] = playerStatus;
        }
        this.updatePlayersStatus();
        this.addPropertyChangeListener(this.lobbyModel, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("playersInLobby")) {
                    LobbyScreen.this.updatePlayersStatus();
                }
            }
        });
        if (this.host) {
            this.readyButton = new TextButton("Lancer la partie", skin);
            this.readyButton.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
            this.readyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    if (allReady()) {
                        Connection connection = ConnectionManager.getInstance().getConnection();
                        if (connection instanceof ServerConnection) {
                            ServerConnection serverConnection = (ServerConnection) connection;
                            serverConnection.getPlayer().getLobby().setState(GameState.IN_LOADING);
                            serverConnection.getPlayer().setLoading(true);
                            ServerSenderReceiver[] clients = serverConnection.getClients();
                            for (ServerSenderReceiver client : clients) {
                                if (client != null) {
                                    PlayerModel player = client.getPlayer();
                                    if (player != null) {
                                        player.setLoading(true);
                                        client.send(new LoadingStartsPacket());
                                    }
                                }
                            }
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    LobbyScreen.this.dispose();
                                    BatailleNavale.getInstance().setScreen(new LoadingScreen());
                                }
                            });
                            ConnectionManager.getInstance().stopLocalServices();
                        }
                    }
                }
            });
        } else {
            this.readyButton = new TextButton("Pret", skin);
            this.readyButton.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
            this.readyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    Connection connection = ConnectionManager.getInstance().getConnection();
                    if (connection != null) {
                        PlayerModel player = connection.getPlayer();
                        if (player != null) {
                            connection.sendPacket(new ReadyStateChangePacket(player.getPlayerUUID()));
                        }
                    }
                }
            });
        }
        this.table.add(readyButton).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.065f).padTop(GameConstants.SCREEN_HEIGHT * 0.01f);
        this.table.row();
        this.messageWriter = new TextField("", skin);
        this.sendButton = new ImageButton(skin);
        this.chat = new Label(text, skin);
        this.chat.setColor(Color.BLACK);
        this.chat.setWrap(true);
        this.chat.setAlignment(Align.bottomLeft);
        this.chat.getStyle().font.getData().setScale(GameConstants.FONT_SCALE);
        this.scroller = new ScrollPane(this.chat);
        this.scroller.setScrollingDisabled(true, false);
        this.scroller.layout();
        this.stage.addActor(this.optionsMenu);
        this.table.setFillParent(true);
        this.table.row();
        this.table.add(this.scroller).expandY().align(Align.bottom).fill();
        this.table.row();
        this.table.add(this.messageWriter).height(GameConstants.SCREEN_HEIGHT * 0.06F).width(GameConstants.SCREEN_WIDTH * 0.7F);
        this.table.add(this.sendButton).size(GameConstants.SCREEN_WIDTH * 0.1f, GameConstants.SCREEN_HEIGHT * 0.055f);
        this.stage.setKeyboardFocus(this.messageWriter);
        this.messageWriter.setAlignment(Align.center);
        this.messageWriter.getStyle().font.getData().setScale(GameConstants.FONT_SCALE);
        this.messageWriter.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (((c == '\r') || (c == '\n')) && !textField.getText().trim().isEmpty()) {
                    ConnectionManager connectionManager = ConnectionManager.getInstance();
                    if (connectionManager != null) {
                        Connection connection = connectionManager.getConnection();
                        if (connection != null) {
                            PlayerModel player = connection.getPlayer();
                            if (player != null) {
                                String message = player.getUsername() + ": " + LobbyScreen.this.messageWriter.getText();
                                ConnectionManager.getInstance().sendPacket(new ChatMessagePacket(player.getPlayerUUID(), message));
                                if (LobbyScreen.this.host) {
                                    LobbyScreen.this.addMessage(message, false);
                                }
                            }
                        }
                    }
                    textField.setText("");
                }
            }
        });
        this.sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (!LobbyScreen.this.messageWriter.getText().trim().isEmpty()) {
                    ConnectionManager connectionManager = ConnectionManager.getInstance();
                    if (connectionManager != null) {
                        Connection connection = connectionManager.getConnection();
                        if (connection != null) {
                            PlayerModel player = connection.getPlayer();
                            if (player != null) {
                                String message = player.getUsername() + ": " + LobbyScreen.this.messageWriter.getText();
                                ConnectionManager.getInstance().sendPacket(new ChatMessagePacket(player.getPlayerUUID(), message));
                                if (LobbyScreen.this.host) {
                                    LobbyScreen.this.addMessage(message, false);
                                }
                            }
                        }
                    }
                    LobbyScreen.this.messageWriter.setText("");
                }
            }
        });
        this.returnHome = new TextButton("Retour Menu", skin);
        this.returnHome.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.returnHome.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        ConnectionManager.getInstance().closeConnection();
                        LobbyScreen.this.dispose();
                        BatailleNavale.getInstance().setScreen(new MainMenuScreen());
                    }
                });
            }
        });
        this.table.row();
        this.table.add(this.returnHome).size(GameConstants.SCREEN_WIDTH * 0.5f, GameConstants.SCREEN_HEIGHT * 0.06f).padTop(GameConstants.SCREEN_HEIGHT * 0.015f).padBottom(GameConstants.SCREEN_HEIGHT * 0.01f);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
        this.scroller.layout();
        this.scroller.scrollTo(0, 0, 0, 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.4F, 1F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(delta);
        this.stage.draw();
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            BatailleNavale.getInstance().setScreen(new MainMenuScreen());
            ConnectionManager.getInstance().closeConnection();
            this.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
        this.stage.dispose();
        ConnectionManager.getInstance().stopLobbyDiscovery();
    }

    /**
     * ajoute un message au chat
     */
    public void addMessage(String message, boolean toastText) {
        if (toastText) {
            BatailleNavale.getInstance().showText(message, true);
        }
        this.chat.setText(chat.getText() + "\n" + message);
        this.scroller.layout();
        this.scroller.scrollTo(0, 0, 0, 0);
    }

    /**
     * rafraichit le status des joueurs
     */
    private void updatePlayersStatus() {
        PlayerModel[] playersInLobby = this.lobbyModel.getPlayersInLobby();
        int i = 0;
        for (; i < playersInLobby.length; i++) {
            final PlayerModel playerInLobby = playersInLobby[i];
            final TextField playerStatus = this.playersStatus[i];
            playerStatus.setColor(playerInLobby.isReady() ? Color.GREEN : Color.RED);
            playerStatus.setText(playerInLobby.getUsername());
            playerInLobby.clearPropertyChangeListeners();
            this.addPropertyChangeListener(playerInLobby, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String propertyName = evt.getPropertyName();
                    if (propertyName.equals("username")) {
                        playerStatus.setText((String) evt.getNewValue());
                    } else if (propertyName.equals("ready")) {
                        playerStatus.setColor((boolean) evt.getNewValue() ? Color.GREEN : Color.RED);
                    }
                }
            });
            if (i >= 1 && this.host) {
                TextButton kickButton = kickButtons[i - 1];
                kickButton.clearListeners();
                kickButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        Connection connection = ConnectionManager.getInstance().getConnection();
                        if (connection instanceof ServerConnection) {
                            UUID playerUUID = playerInLobby.getPlayerUUID();
                            ServerSenderReceiver client = ((ServerConnection) connection).getClientByUUID(playerUUID);
                            if (client != null) {
                                connection.sendPacket(new KickPlayerPacket(playerUUID));
                                ((ServerConnection) connection).removeClient(client);
                                connection.getPlayer().getLobby().removePlayer(client.getPlayer());
                            }
                        }
                    }
                });
                kickButton.setDisabled(false);
            }
        }
        for (int j = i; j < this.playersStatus.length; j++) {
            final TextField playerStatus = this.playersStatus[j];
            playerStatus.setColor(Color.RED);
            playerStatus.setText("");
            if (j >= 1 && this.host) {
                TextButton kickButton = kickButtons[j - 1];
                kickButton.clearListeners();
                kickButton.setDisabled(true);
            }
        }
    }

    /**
     * regarde si tous les joueurs sont pret
     */
    private boolean allReady() {
        PlayerModel[] playersInLobby = this.lobbyModel.getPlayersInLobby();
        if (playersInLobby.length <= 1)
            return false;
        for (PlayerModel pl : playersInLobby) {
            if (!pl.isReady() && !(pl instanceof ClientPlayerModel))
                return false;
        }
        return true;
    }
}
