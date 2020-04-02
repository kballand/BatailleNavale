package fr.bataillenavale.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

import fr.bataillenavale.exception.InvalidTilePositionException;
import fr.bataillenavale.game.Tile;
import fr.bataillenavale.utils.GameUtils;

/**
 * Case animee d'une parcelle d'ocean
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public abstract class AnimatedTile extends Tile {
    // AnimatedTexture de la case
    private Animation<Texture> animation;
    // Booleen indiquant si l'animation est en boucle ou non
    private boolean looping;
    // Temps ecoule depuis l'affichage de la premiere frame
    private float stateTime;

    /**
     * Constructeur d'une case animee
     *
     * @param x             Abscisse de la case
     * @param y             Ordonnee de la case
     * @param frameDuration Duree d'une frame de l'animation
     * @param looping       Booleen indiquant si l'animation est en boucle ou non
     * @throws InvalidTilePositionException En cas de position invalide de la case
     */
    public AnimatedTile(int x, int y, float frameDuration, boolean looping) throws InvalidTilePositionException {
        super(x, y);
        Texture[] animationFrames = GameUtils.getAllTextures(this.getTextureName(), this.isInGame());
        this.animation = new Animation<>(frameDuration, animationFrames);
        this.looping = looping;
        this.stateTime = 0f;
    }


    public void reloadAnimation() {
        Texture[] animationFrames = GameUtils.getAllTextures(this.getTextureName(), this.isInGame());
        this.animation = new Animation<>(this.animation.getFrameDuration(), animationFrames);
    }

    @Override
    public Sprite getSprite() {
        stateTime += Gdx.graphics.getDeltaTime();
        Texture texture = this.animation.getKeyFrame(this.stateTime, this.looping);
        return new Sprite(texture);
    }
}
