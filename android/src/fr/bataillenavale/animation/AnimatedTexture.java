package fr.bataillenavale.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import fr.bataillenavale.game.Drawable;
import fr.bataillenavale.utils.GameUtils;


public abstract class AnimatedTexture implements Drawable {
    // Animation de la case
    private Animation<Texture> animation;
    // Booleen indiquant si l'animation est en boucle ou non
    private boolean looping;
    // Temps ecoule depuis l'affichage de la premiere frame
    private float stateTime;
    // Position de la texture
    private Vector2 position;
    private float size;

    /**
     * Constructeur d'une texture animee
     *
     * @param position      Position de base de la texture animee
     * @param frameDuration Duree d'une frame de l'animation
     * @param looping       Booleen indiquant si l'animation est en boucle ou non
     */
    public AnimatedTexture(Vector2 position, float frameDuration, boolean looping, float size) {
        this.position = position;
        Texture[] animationFrames = GameUtils.getAllTextures(this.getTextureName(), this.isInGame());
        this.animation = new Animation<>(frameDuration, animationFrames);
        this.looping = looping;
        this.stateTime = 0f;
        this.size = size;
    }

    @Override
    public Sprite getSprite() {
        stateTime += Gdx.graphics.getDeltaTime();
        Texture texture = this.animation.getKeyFrame(this.stateTime, this.looping);
        Sprite sprite = new Sprite(texture);
        sprite.setPosition(position.x, position.y);
        sprite.setSize(this.size, this.size);
        return sprite;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public boolean isFinished() {
        return this.animation.isAnimationFinished(this.stateTime);
    }

    public interface OnAnimationFinishListener {
        void run();
    }
}
