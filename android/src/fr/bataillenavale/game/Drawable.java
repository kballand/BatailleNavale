package fr.bataillenavale.game;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Objet dessinable
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public interface Drawable {
    /**
     * Methode permettant de savoir si l'objet dessinable est uniquement en jeu
     *
     * @return Vrai si l'objet dessinable est en jeu, faux sinon
     */
    boolean isInGame();

    /**
     * Methode permettant de recuperer le nom de la texture de l'objet dessinable
     *
     * @return Le nom de la texture de l'objet dessinable
     */
    String getTextureName();

    /**
     * Methode permettant de recuperer le sprite de l'objet dessinable
     *
     * @return Le sprite de l'objet dessinable
     */
    Sprite getSprite();
}
