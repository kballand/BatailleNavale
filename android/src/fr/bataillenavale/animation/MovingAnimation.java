package fr.bataillenavale.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import fr.bataillenavale.utils.GameConstants;

public abstract class MovingAnimation extends AnimatedTexture {
    private Vector2 finalPosition;
    private float angle;

    /**
     * Constructeur d'une texture mouvante
     *
     * @param position      Position de base de la texture mouvante
     * @param finalPosition Position finale de la texture mouvante
     * @param frameDuration Duree d'une frame de l'animation
     * @param looping       Booleen indiquant si l'animation est en boucle ou non
     */
    public MovingAnimation(Vector2 position, Vector2 finalPosition, float frameDuration, boolean looping, float size) {
        super(position, frameDuration, looping, size);
        this.finalPosition = finalPosition;
        this.angle = (float) Math.atan2(finalPosition.y - position.y, finalPosition.x - position.x);
    }

    public Vector2 getFinalPosition() {
        return this.finalPosition;
    }

    public void setFinalPosition(Vector2 finalPosition) {
        this.finalPosition = finalPosition;
    }

    public float getAngle() {
        return this.angle;
    }

    private void move() {
        this.getPosition().x += (float) Math.cos(angle) * GameConstants.ANIMATION_SPEED * Gdx.graphics.getDeltaTime();
        this.getPosition().y += (float) Math.sin(angle) * GameConstants.ANIMATION_SPEED * Gdx.graphics.getDeltaTime();
    }

    @Override
    public Sprite getSprite() {
        Sprite sprite = super.getSprite();
        sprite.setRotation((float) Math.toDegrees(this.angle));
        this.move();
        return sprite;
    }

    @Override
    public boolean isFinished() {
        return (float) (Math.round(Math.atan2(finalPosition.y - this.getPosition().y, finalPosition.x - this.getPosition().x) * 100) / 100) != (float) (Math.round(angle * 100) / 100);
    }
}
