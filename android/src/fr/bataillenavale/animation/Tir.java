package fr.bataillenavale.animation;

import com.badlogic.gdx.math.Vector2;

import fr.bataillenavale.utils.GameConstants;

public class Tir extends MovingAnimation {

    /**
     * Constructeur d'une texture mouvante
     *
     * @param position      Position de base de la texture mouvante
     * @param finalPosition Position finale de la texture mouvante
     * @param frameDuration Duree d'une frame de l'animation
     */
    public Tir(Vector2 position, Vector2 finalPosition, float frameDuration) {
        super(position, finalPosition, frameDuration, true, GameConstants.ANIMATION_SIZE);
    }

    @Override
    public boolean isInGame() {
        return true;
    }

    @Override
    public String getTextureName() {
        return "missile";
    }
}
