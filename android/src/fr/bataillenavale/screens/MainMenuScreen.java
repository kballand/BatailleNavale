package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe d'affichage pour le menu principal
 */
public class MainMenuScreen implements Screen {

	/**
	 * scene en cours
	 */
    private Stage stage;
    /**
     * table
     */
    private Table table;
    /**
     * titre du jeu
     */
    private Label title;
    /**
     * bouton pour creer un salon
     */
    private TextButton createLobby;
    /**
     * bouton  pour rejoindre un salon
     */
    private TextButton joinLobby;
    /**
     * bouton pour aller dans les options
     */
    private TextButton optionsMenu;
    /**
     * bouton pour squitter le jeu
     */
    private TextButton exitButton;
    /**
     * texture
     */
    private Skin skin;

    /**
     * constructeur de MainMenuScreen
     */
    public MainMenuScreen() {
        super();
        this.skin = GameUtils.getDefaultSkin();
//        this.skin.
        this.stage = new Stage(BatailleNavale.getInstance().getViewport());
        this.table = new Table();
        this.stage.addActor(this.table);
        this.table.setFillParent(true);
        this.title = new Label("Bataille Navale", new Label.LabelStyle(skin.getFont("title"), Color.WHITE));
        this.title.getStyle().font.getData().setScale(GameConstants.TITLE_SCALE);
        this.createLobby = new TextButton("Creer un salon", GameUtils.getDefaultSkin());
        this.createLobby.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.createLobby.pad(GameConstants.BUTTON_PADDING);
        this.createLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                MainMenuScreen.this.dispose();
                BatailleNavale.getInstance().setScreen(new LobbyCreationScreen());
            }
        });
        this.joinLobby = new TextButton("Rejoindre un salon", GameUtils.getDefaultSkin());
        this.joinLobby.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.joinLobby.pad(GameConstants.BUTTON_PADDING);
        this.joinLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                MainMenuScreen.this.dispose();
                BatailleNavale.getInstance().setScreen(new LobbySearchScreen(ConnectionManager.getInstance().getLobbyList()));
            }
        });
        this.optionsMenu = new TextButton("Acceder aux options", GameUtils.getDefaultSkin());
        this.optionsMenu.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.optionsMenu.pad(GameConstants.BUTTON_PADDING);
        this.optionsMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                BatailleNavale.getInstance().setScreen(new OptionsScreen(MainMenuScreen.this));
            }
        });
        this.exitButton = new TextButton("Quitter le jeu", GameUtils.getDefaultSkin());
        this.exitButton.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.exitButton.pad(GameConstants.BUTTON_PADDING);
        this.exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                Gdx.app.exit();
                System.exit(0);
            }
        });

        this.table.add(this.title);
        this.table.row();
        this.table.add(this.createLobby).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(GameConstants.BUTTON_PADDING).padTop(GameConstants.TITLE_SPACE);
        this.table.row();
        this.table.add(this.joinLobby).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(GameConstants.BUTTON_PADDING);
        this.table.row();
        this.table.add(this.optionsMenu).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(GameConstants.BUTTON_PADDING);
        this.table.row();
        this.table.add(this.exitButton).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(GameConstants.BUTTON_PADDING);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }


    @Override
    public void render(float delta) {
        SpriteBatch batch = BatailleNavale.getInstance().getSpriteBatch();
        batch.setProjectionMatrix(BatailleNavale.getInstance().getViewport().getCamera().combined);
        Texture background = GameUtils.getTexture("background.png", false);
        Sprite backgroundSprite = new Sprite(background);
        Gdx.gl.glClearColor(0, 0.4F, 1F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        backgroundSprite.setSize(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        backgroundSprite.setCenter(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT / 2);
        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        this.stage.act(delta);
        this.stage.draw();
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
