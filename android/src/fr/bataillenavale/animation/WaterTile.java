package fr.bataillenavale.animation;

import fr.bataillenavale.exception.InvalidTilePositionException;

/**
 * Case d'eau
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class WaterTile extends AnimatedTile {
    private boolean destroyed;

    /**
     * Constructeur d'une case d'eau
     *
     * @param x Abscisse de la case d'eau
     * @param y Ordonnee de la case d'eau
     * @throws InvalidTilePositionException En cas d'erreur de la position de la case d'eau
     */
    public WaterTile(int x, int y) throws InvalidTilePositionException {
        super(x, y, 0.333f, true);
    }

    @Override
    public String getTextureName() {
        return "mer" + (this.destroyed ? "_rate" : "");
    }

    @Override
    public boolean isInGame() {
        return true;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        if(this.destroyed != destroyed) {
            this.destroyed = destroyed;
            this.reloadAnimation();
        }
    }
}
