package fr.bataillenavale.game;

import android.widget.Toast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.PlayerUsernameChangePacket;
import fr.bataillenavale.screens.MainMenuScreen;

/**
 * Classe representant le jeu de Bataille Navale
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class BatailleNavale extends Game {

    // Instance unique du jeu
    private static BatailleNavale instance;

    // La camera du jeu
    private OrthographicCamera camera;
    // La methode d'affichage des vues
    private ScreenViewport viewport;
    // La vue actuelle du jeu
    private Screen activeScreen;
    // Les preferences du joueurs (options)
    private Preferences preferences;
    // Dessineur de formes
    private ShapeRenderer shapeRenderer;
    // Dessineur de sprite
    private SpriteBatch spriteBatch;
    // Gestionnaire des textures
    private AssetManager assetManager;

    /**
     * Constructeur du jeu
     */
    public BatailleNavale() {
        super();
        instance = this;
    }

    /**
     * Méthode permettant de recupérer l'instance unique du jeu
     *
     * @return L'instance unique du jeu
     */
    public static BatailleNavale getInstance() {
        return instance;
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.camera);
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.assetManager = new AssetManager();
        this.assetManager.load("resources/images/default/background.png", Texture.class);
//        this.assetManager.load("resources/images/bouleFeu.png", Texture.class);
        this.loadDefaultTextures();
        this.loadInGameTextures();
        this.assetManager.finishLoading();
        this.setupPreferences();
        this.setScreen(new MainMenuScreen());
        ConnectionManager.setupConnection();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.camera.setToOrtho(false, width, height);
        this.activeScreen.resize(width, height);
        this.camera.update();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        this.activeScreen = screen;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.shapeRenderer.dispose();
        this.assetManager.dispose();
        this.activeScreen.dispose();
    }

    /**
     * Met en place les preferences de base si elles ne sont pas definies
     */
    private void setupPreferences() {
        this.preferences = Gdx.app.getPreferences("Options");
        if (!this.preferences.contains("username")) {
            this.setUsername("default");
        }
        if (!this.preferences.contains("volume")) {
            this.setVolume(50);
        }
        if (!this.preferences.contains("animated")) {
            this.setAnimated(true);
        }
        this.preferences.flush();
    }

    /**
     * Getter du viewport
     *
     * @return Retourne le viewport
     */
    public Viewport getViewport() {
        return this.viewport;
    }

    /**
     * Getter du nom de l'utilisateur
     *
     * @return Le nom de l'utilisateur
     */
    public String getUsername() {
        return this.preferences.getString("username");
    }

    /**
     * Setter du nom d'utilisateur
     *
     * @param username Le nouveau nom d'utilisateur
     */
    public void setUsername(String username) {
        if (!this.getUsername().equals(username)) {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            if (connectionManager != null) {
                Connection connection = connectionManager.getConnection();
                if (connection != null) {
                    PlayerModel player = connection.getPlayer();
                    if (player != null) {
                        connection.sendPacket(new PlayerUsernameChangePacket(player.getPlayerUUID(), username));
                        if (connection instanceof ServerConnection) {
                            player.setUsername(username);
                        }
                    }
                }
            }
            this.preferences.putString("username", username);
            this.preferences.flush();
        }
    }

    /**
     * Getter du volume du jeu
     *
     * @return Le volume du jeu
     */
    public int getVolume() {
        return this.preferences.getInteger("volume");
    }

    /**
     * Setter du volume du jeu
     *
     * @param volume Le nouveau volume du jeu
     */
    public void setVolume(int volume) {
        this.preferences.putInteger("volume", volume);
        this.preferences.flush();
    }

    /**
     * Getter du fait que le jeu affiche les animations ou non
     *
     * @return Le fait que le jeu affiche les animations ou non
     */
    public boolean isAnimated() {
        return this.preferences.getBoolean("animated");
    }

    /**
     * Setter de fait que le jeu affiche les animations ou non
     *
     * @param animated Vrai pour afficher les animations, sinon faux
     */
    public void setAnimated(boolean animated) {
        this.preferences.putBoolean("animated", animated);
        this.preferences.flush();
    }

    /**
     * Permet d'afficher du texte à l'écran sous forme de popup (Toast)
     *
     * @param text         Texte à afficher à l'écran
     * @param durationLong Vrai pour une longue durée, faux pour une courte durée
     */
    public void showText(final String text, final boolean durationLong) {
        AndroidLauncher.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AndroidLauncher.getInstance().getApplicationContext(), text, durationLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Methode permettant de recuperer le dessineur de formes
     *
     * @return Le dessineur de formes
     */
    public ShapeRenderer getShapeRenderer() {
        return this.shapeRenderer;
    }

    /**
     * Methode permettant de recuperer le dessineur de textures
     *
     * @return Le dessineur de sprites
     */
    public SpriteBatch getSpriteBatch() {
        return this.spriteBatch;
    }

    /**
     * Methode permettant de recuperer le gestionnaire de textures
     *
     * @return Le gestionnaire de textures
     */
    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    /**
     * Methode permettant de charger toutes les textures necessaires pour jouer au jeu
     */
    public void loadInGameTextures() {
        this.loadAllTextures("resources/images/ingame");
    }

    /**
     * Methode permettant de charger toutes les textures necessaires par defaut
     */
    public void loadDefaultTextures() {
        this.loadAllTextures("resources/images/default");
    }

    /**
     * Methode privee permettant de charger toutes les textures d'un repertoire
     *
     * @param assetsPath Repertoire avec les textures a charger
     */
    private void loadAllTextures(String assetsPath) {
        FileHandle assetsDirectory = Gdx.files.internal(assetsPath);
        if (assetsDirectory.exists() && assetsDirectory.isDirectory()) {
            for (FileHandle asset : assetsDirectory.list()) {
                if (asset.exists() && !asset.isDirectory()) {
                    String path = asset.path();
                    if (!this.assetManager.contains(path)) {
                        this.assetManager.load(path, Texture.class);
                    }
                }
            }
        }
    }
}
