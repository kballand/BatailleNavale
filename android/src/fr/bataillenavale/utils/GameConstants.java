package fr.bataillenavale.utils;

import com.badlogic.gdx.Gdx;

/**
 * Classe regroupant les constante du jeu
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class GameConstants {
    // Url de l'apparence par defaut des widget de l'application
    public final static String DEFAULT_SKIN_PATH = "resources/comic/comic-ui.json";
    // Largeur de l'ecran
    public final static float SCREEN_WIDTH = Gdx.graphics.getWidth();
    // Hauteur de l'ecran
    public final static float SCREEN_HEIGHT = Gdx.graphics.getHeight();
    // Multiplicateur de taille du titre de l'ecran principal
    public final static float TITLE_SCALE = (GameConstants.SCREEN_HEIGHT / 1920.0F) * 2.5f;

    public final static float BUTTON_SCALE = (GameConstants.SCREEN_HEIGHT / 1700.0F);

    public final static float FONT_SCALE = (GameConstants.SCREEN_HEIGHT / 1400.0F);
    // Padding des boutons
    public final static float BUTTON_PADDING = GameConstants.SCREEN_HEIGHT * 0.03F;
    // Espace entre le titre de l'ecran principal et les boutons
    public final static float TITLE_SPACE = GameConstants.SCREEN_HEIGHT * 0.18F;
    public final static String SERVICE_NAME = "_bataillenavale._tcp";
    public final static int LISTENING_PORT = 24172;
    public final static int OCEAN_CHUNK_SIZE = 10;
    public final static int[] SHIPS_SIZE_PER_PLAYER = {2, 3, 4, 4, 5};
    public final static int SHIPS_PER_PLAYER = SHIPS_SIZE_PER_PLAYER.length;
    public final static String[] SHIPS_NAME = {"sous-marin", "torpilleur", "destroyer", "porte-avion"};
    public final static int MIN_SHIP_LENGTH = 2;
    public final static int MAX_SHIP_LENGTH = 5;
    public final static String TEXTURES_PATH = "resources/images/";
    public final static float ANIMATION_SIZE = SCREEN_WIDTH / 10F;
    public final static float ANIMATION_SPEED = (GameConstants.SCREEN_HEIGHT / 1920.0F) * 450F;
}
