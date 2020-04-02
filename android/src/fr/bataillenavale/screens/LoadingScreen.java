package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import fr.bataillenavale.connection.ClientConnection;
import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.packet.LoadingFinishedPacket;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe pour l'affichage du chargement 
 */
public class LoadingScreen implements Screen {

	/**
     * scene de l'affichage
     */
    private Stage stage;
    /**
     * table
     */
    private Table table;
    /**
     * barre de progression
     */
    private ProgressBar progressBar;

    /**
     * constructeur de LoadingScreen
     */
    public LoadingScreen() {
        this.stage = new Stage(BatailleNavale.getInstance().getViewport());
        this.table = new Table();
        this.stage.addActor(this.table);
        this.progressBar = new ProgressBar(0, 100, 1, false, GameUtils.getDefaultSkin());
        this.table.setFillParent(true);
        this.table.add(this.progressBar).width(GameConstants.SCREEN_WIDTH * 0.75F).height(GameConstants.SCREEN_HEIGHT * 0.05F).expandY().fillY().bottom().pad(30);
        BatailleNavale.getInstance().loadInGameTextures();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch batch = BatailleNavale.getInstance().getSpriteBatch();
        batch.setProjectionMatrix(BatailleNavale.getInstance().getViewport().getCamera().combined);
        AssetManager assetManager = BatailleNavale.getInstance().getAssetManager();
        Texture background = GameUtils.getTexture("background.png", false);
        Sprite backgroundSprite = new Sprite(background);
        backgroundSprite.setSize(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        backgroundSprite.setCenter(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT / 2);
        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();
        this.progressBar.setValue(assetManager.getProgress() * 100);
        if (assetManager.update()) {
            Connection connection = ConnectionManager.getInstance().getConnection();
            connection.getPlayer().setLoading(false);
            if (connection instanceof ServerConnection) {
                ServerConnection serverConnection = (ServerConnection) connection;
                if (serverConnection.canStartGame()) {
                    serverConnection.startGame();
                }
            } else if (connection instanceof ClientConnection) {
                connection.sendPacket(new LoadingFinishedPacket());
            }
        }
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
