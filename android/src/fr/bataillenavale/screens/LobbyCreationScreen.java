package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe pour l'ecran de la creation de salon
 */
public class LobbyCreationScreen implements Screen {

	/**
     * scene de l'ecran
     */
    private Stage stage;
    /**
     * table
     */
    private Table table;
    /**
     * nom du salon
     */
    private Label lobbyNameLabel;
    /**
     * permet de saisir le nom du salon
     */
    private TextField lobbyName;
    /**
     * prend le nom du joueur
     */
    private Label lobbyPlayersLabel;
    /**
     * nombre de joueurs possibles
     */
    private Slider lobbyPlayersNumber;
    /**
     *bouton pour creer le salon
     */
    private TextButton createLobby;
    /**
     * bouton pour retourner au menu principal
     */
    private TextButton returnHome;

    /**
     * constructeur de LobbyCreationScreen
     */
    public LobbyCreationScreen() {
        this.stage = new Stage(BatailleNavale.getInstance().getViewport());
        this.table = new Table();
        this.stage.addActor(this.table);
        this.lobbyNameLabel = new Label("Nom du salon", GameUtils.getDefaultSkin());
        this.lobbyNameLabel.getStyle().font.getData().setScale(GameConstants.FONT_SCALE);
        this.lobbyName = new TextField("   Salon de " + BatailleNavale.getInstance().getUsername(), GameUtils.getDefaultSkin());
        this.lobbyName.setMaxLength(30);
        this.lobbyName.getStyle().font.getData().setScale(GameConstants.FONT_SCALE);
        this.lobbyName.setAlignment(Align.left);
        this.lobbyPlayersLabel = new Label("Joueurs maximum : 1/4", GameUtils.getDefaultSkin());
        this.lobbyPlayersLabel.getStyle().font.getData().setScale(GameConstants.FONT_SCALE);
        this.lobbyPlayersNumber = new Slider(1, 4, 1, false, GameUtils.getDefaultSkin());
        this.lobbyPlayersNumber.getStyle().knob.setMinHeight(40);
        this.lobbyPlayersNumber.getStyle().knob.setMinWidth(25);
        this.lobbyPlayersNumber.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int playersNumber = (int) LobbyCreationScreen.this.lobbyPlayersNumber.getValue();
                LobbyCreationScreen.this.lobbyPlayersLabel.setText("Joueurs maximum : " + playersNumber + "/4");
            }
        });
        this.createLobby = new TextButton("Créer le salon", GameUtils.getDefaultSkin());
        this.createLobby.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.createLobby.pad(30);
        this.createLobby.setDisabled(false);
        this.createLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (!LobbyCreationScreen.this.lobbyName.getText().isEmpty()) {
                    ConnectionManager.getInstance().startHosting(LobbyCreationScreen.this.lobbyName.getText(), (int) LobbyCreationScreen.this.lobbyPlayersNumber.getValue());
                } else {
                    BatailleNavale.getInstance().showText("Vous devez entrer un nom de salon !", false);
                }
            }
        });
        this.returnHome = new TextButton("Retour Menu", GameUtils.getDefaultSkin());
        this.returnHome.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.returnHome.pad(GameConstants.BUTTON_PADDING);
        this.returnHome.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                LobbyCreationScreen.this.dispose();
                BatailleNavale.getInstance().setScreen(new MainMenuScreen());
            }
        });
        this.table.setFillParent(true);
        this.table.add(this.lobbyNameLabel).pad(20);
        this.table.row();
        this.table.add(this.lobbyName).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).height(100).width(GameConstants.SCREEN_WIDTH * 0.5F).pad(20);
        this.table.row();
        this.table.add(this.lobbyPlayersLabel).pad(20).padTop(50);
        this.table.row();
        this.table.add(this.lobbyPlayersNumber).height(100).width(GameConstants.SCREEN_WIDTH * 0.5F).pad(20);
        this.table.row();
        this.table.add(this.createLobby).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(20).padTop(100);
        this.table.row();
        this.table.add(this.returnHome).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(20).padTop(100);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0, 1);
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
        this.stage.dispose();
    }
}
