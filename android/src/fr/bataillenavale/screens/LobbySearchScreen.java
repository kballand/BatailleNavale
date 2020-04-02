package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.models.LobbyConnection;
import fr.bataillenavale.models.LobbyListModel;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe d'affichage pour la recherche de salon
 */
public class LobbySearchScreen extends NotifiableScreen {
	/**
	 * Liste des salons
	 */
    private LobbyListModel lobbyListModel;
    /**
     * scene 
     */
    private Stage stage;
    /**
     * table
     */
    private Table table;
    /**
     * salon disponible
     */
    private Table availableLobby;
    /**
     * texte "recherche"
     */
    private Label searching;
    private TextButton.TextButtonStyle lobbyButtonStyle;
    /**
     * permet de naviguer de haut en bas
     */
    private ScrollPane scroller;
    /**
     * bouton pour le menu d'options
     */
    private ImageButton optionsMenu;
    /**
     * bouton pour rafraichir
     */
    private TextButton refresh;
    /**
     * bouton pour retourner au menu principal
     */
    private TextButton returnHome;

    /**
     * constructeur de LobbySearchScreen
     * @param lobbyListModel LobbyListModel
     */
    public LobbySearchScreen(LobbyListModel lobbyListModel) {
        super();
        this.lobbyListModel = lobbyListModel;
        this.stage = new Stage(BatailleNavale.getInstance().getViewport());
        this.table = new Table();
        this.availableLobby = new Table();
        this.scroller = new ScrollPane(this.availableLobby);
        this.scroller.setScrollingDisabled(true, false);
        this.scroller.layout();
        this.table.add(this.scroller).expandY().top().fillX().expandX();
        this.stage.addActor(this.table);
        this.table.setFillParent(true);
        this.table.pad(75);
        this.table.top();
        this.searching = new Label("Recherche de nouveaux salons à proximité...", GameUtils.getDefaultSkin());
        this.searching.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.searching.setAlignment(Align.center);
        this.searching.setWrap(true);
        this.lobbyButtonStyle = GameUtils.getDefaultSkin().get(TextButton.TextButtonStyle.class);
        this.availableLobby.add(this.searching);
        this.optionsMenu = GameUtils.getImageButton("options.png", false);

        this.optionsMenu.setSize((75 * GameConstants.SCREEN_HEIGHT) / 1920F, (75 * GameConstants.SCREEN_HEIGHT) / 1920F);
        this.optionsMenu.setPosition(GameConstants.SCREEN_WIDTH - (75 * GameConstants.SCREEN_HEIGHT) / 1920F, GameConstants.SCREEN_HEIGHT - (75 * GameConstants.SCREEN_HEIGHT) / 1920F);
        this.refresh = new TextButton("Rafraîchir la recherche", GameUtils.getDefaultSkin());
        this.refresh.pad(GameConstants.BUTTON_PADDING);
        this.refresh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                ConnectionManager.getInstance().getLobbyList().clearAvailableLobby();
                ConnectionManager.getInstance().stopLobbyDiscovery();
                ConnectionManager.getInstance().startLobbyDiscovery();
            }
        });
        this.returnHome = new TextButton("Retour Menu", GameUtils.getDefaultSkin());
        this.returnHome.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.returnHome.pad(GameConstants.BUTTON_PADDING);
        this.returnHome.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.returnHome.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                LobbySearchScreen.this.dispose();
                BatailleNavale.getInstance().setScreen(new MainMenuScreen());
            }
        });
        this.stage.addActor(this.optionsMenu);
        this.optionsMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                BatailleNavale.getInstance().setScreen(new OptionsScreen(LobbySearchScreen.this));
            }
        });
        this.table.row();
        this.table.add(this.refresh).bottom().pad(20);
        this.availableLobby.row();
        this.availableLobby.add(this.returnHome).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(20).padTop(100);
        ConnectionManager.getInstance().startLobbyDiscovery();
        this.addPropertyChangeListener(this.lobbyListModel, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("lobbyConnections")) {
                    LobbyConnection[] availableLobby = (LobbyConnection[]) evt.getNewValue();
                    LobbySearchScreen.this.availableLobby.clearChildren();
                    if (availableLobby.length > 0) {
                        for (final LobbyConnection lobbyConnection : availableLobby) {
                            TextButton button = new TextButton(lobbyConnection.getLobbyName(), LobbySearchScreen.this.lobbyButtonStyle);
                            button.getLabel().setWrap(true);
                            button.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent e, float x, float y) {
                                    ConnectionManager.getInstance().connectTo(lobbyConnection);
                                }
                            });
                            LobbySearchScreen.this.availableLobby.add(button).fillX().expandX();
                            LobbySearchScreen.this.availableLobby.row();
                        }
                        if (availableLobby.length > ((LobbyConnection[]) evt.getOldValue()).length) {
                            BatailleNavale.getInstance().showText("Nouveau salon trouvé !", false);
                        }
                    } else {
                        LobbySearchScreen.this.availableLobby.add(LobbySearchScreen.this.searching).fillX().expandX();
                    }
                }
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4F, 0.4F, 0.4F, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(delta);
        this.stage.draw();
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            BatailleNavale.getInstance().setScreen(new MainMenuScreen());
            this.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
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
        this.lobbyListModel.clearAvailableLobby();
    }
}
