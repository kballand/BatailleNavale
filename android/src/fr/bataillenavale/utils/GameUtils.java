package fr.bataillenavale.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;
import fr.bataillenavale.game.AndroidLauncher;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.Ship;
import fr.bataillenavale.game.ShipPart;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;

import static fr.bataillenavale.utils.GameConstants.OCEAN_CHUNK_SIZE;
import static fr.bataillenavale.utils.GameConstants.SCREEN_HEIGHT;
import static fr.bataillenavale.utils.GameConstants.SCREEN_WIDTH;

/**
 * Classe representant l'ensemble des outils du jeu
 *
 * @author Killian BALLAND, Louis MATUCHET, atthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class GameUtils {
    /**
     * Permet de recuperer l'apparence par defaut des widgets du jeu
     *
     * @return Le skin par defaut des widgets du jeu
     */
    public static Skin getDefaultSkin() {
        return new Skin(Gdx.files.internal(GameConstants.DEFAULT_SKIN_PATH));
    }

    /**
     * Permet de generer un bouton avec image a par d'une image donnee
     *
     * @param imageName Url de l'image pour le bouton
     * @return Le nouveau bouton avec image
     */
    public static ImageButton getImageButton(String imageName, boolean inGame) {
        return new ImageButton(new TextureRegionDrawable(GameUtils.getTexture(imageName, inGame)));
    }

    public static <T> Set<Class<? extends T>> getAllSubClasses(Class<T> parent, boolean samePackage) {
        Set<Class<? extends T>> subClasses = new HashSet<>();
        try {
            DexFile dexFile = new DexFile(AndroidLauncher.getInstance().getPackageCodePath());
            Enumeration<String> classesName = dexFile.entries();
            while (classesName.hasMoreElements()) {
                String className = classesName.nextElement();
                if ((!samePackage || className.startsWith(parent.getPackage().getName())) && !className.contains("$")) {
                    Class<?> subClass = Class.forName(className);
                    if (parent.isAssignableFrom(subClass)) {
                        subClasses.add((Class<? extends T>) subClass);
                    }
                }
            }
        } catch (IOException ignored) {
        } catch (ClassNotFoundException ignored) {
        }
        return subClasses;
    }

    public static boolean isShipsPlacementCorrect(Ship[] ships) {
        if (ships == null)
            return false;
        for (int i = 0; i < ships.length; i++) {
            Ship ship = ships[i];
            if (ship == null)
                return false;
            for (int j = i + 1; j < ships.length; j++) {
                Ship other = ships[j];
                if (other == null)
                    return false;
                for (ShipPart shipPart : ship.getShipParts()) {
                    for (ShipPart otherPart : other.getShipParts()) {
                        if (shipPart.getX() == otherPart.getX() && shipPart.getY() == otherPart.getY())
                            return false;
                    }
                }
            }
        }
        return true;
    }

    public static Texture getTexture(String textureName, boolean inGame) {
        String path = GameConstants.TEXTURES_PATH + (inGame ? "ingame/" : "default/") + textureName;
        return BatailleNavale.getInstance().getAssetManager().get(path, Texture.class);
    }

    public static Texture[] getAllTextures(String textureName, boolean inGame) {
        String path = GameConstants.TEXTURES_PATH + (inGame ? "ingame/" : "default/") + textureName;
        AssetManager assetManager = BatailleNavale.getInstance().getAssetManager();
        List<Texture> lTextures = new ArrayList<>();
        int i = 1;
        while (assetManager.contains(path + i + ".png", Texture.class)) {
            lTextures.add(assetManager.get(path + i++ + ".png", Texture.class));
        }
        Texture[] textures = new Texture[lTextures.size()];
        lTextures.toArray(textures);
        return textures;
    }

    public static Vector2 getTileCenter(LobbyModel lobby, PlayerModel player, int tileX, int tileY) {
        int chunkNumber = lobby.getChunkNumber(player);
        if (chunkNumber != -1) {
            PlayerModel[] playerModels = lobby.getRemainingPlayers();
            int numberOfPlayers = playerModels.length;
            int numberOfPeers = (int) Math.ceil(numberOfPlayers / 2F);
            if (numberOfPeers == 0)
                numberOfPeers = 1;
            boolean odd = numberOfPeers != numberOfPlayers / 2F;

            float gridSize = SCREEN_WIDTH / numberOfPeers;
            float gridMaxHeight = SCREEN_HEIGHT / (numberOfPlayers > 1 ? 1 : 2);

            if ((gridSize > gridMaxHeight / 2) && numberOfPlayers > 1) {
                gridSize = gridMaxHeight / 2;
            } else if (gridSize > gridMaxHeight) {
                gridSize = gridMaxHeight;
            }

            float separatorMargin = gridSize * 0.025F;
            float tileMargin = gridSize * 0.0075F;

            float x;
            float y;

            float tileSize = ((gridSize - (2 * separatorMargin) - ((OCEAN_CHUNK_SIZE - 1) * tileMargin)) / OCEAN_CHUNK_SIZE);
            float extraX = SCREEN_WIDTH - gridSize * numberOfPeers;
            int posX = chunkNumber / 2;
            int posY = chunkNumber % 2;
            x = gridSize * posX + separatorMargin + tileX * (tileSize + tileMargin) + extraX / 2;
            if (tileY == numberOfPlayers - 1 && odd) {
                y = ((SCREEN_HEIGHT / 2) + (gridSize / 2)) - (separatorMargin + tileY * (tileSize + tileMargin) + tileSize);
            } else {
                y = ((SCREEN_HEIGHT / 2) + posY * gridSize) - (separatorMargin + tileY * (tileSize + tileMargin) + tileSize);
            }
            return new Vector2(x + tileSize / 2, y - tileSize / 2);
        }
        return null;
    }
}
