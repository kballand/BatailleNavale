package fr.bataillenavale.game;

import fr.bataillenavale.exception.InvalidTilePositionException;
import fr.bataillenavale.utils.GameConstants;

/**
 * Case d'une parcelle d'ocean
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public abstract class Tile implements Drawable {
    // Abscisse de la case
    private int x;
    // Ordonnee de la clase
    private int y;

    /**
     * Constructeur d'une case
     *
     * @param x Abscisse de la case
     * @param y Ordonnee de la case
     * @throws InvalidTilePositionException Exception levee lorsque la case n'est pas dans la parcelle d'ocean
     */
    public Tile(int x, int y) throws InvalidTilePositionException {
        if (x < 0 || x >= GameConstants.OCEAN_CHUNK_SIZE)
            throw new InvalidTilePositionException("x must be contains between 0 and " + (GameConstants.OCEAN_CHUNK_SIZE - 1) + " !");
        if (y < 0 || y >= GameConstants.OCEAN_CHUNK_SIZE)
            throw new InvalidTilePositionException("y must be contains between 0 and " + (GameConstants.OCEAN_CHUNK_SIZE - 1) + " !");
        this.x = x;
        this.y = y;
    }

    /**
     * Methode permettant d'acceder a l'abscisse de la case
     *
     * @return L'abscisse de la case
     */
    public int getX() {
        return this.x;
    }

    /**
     * Methode permettant d'acceder a l'ordonnee de la case
     *
     * @return L'ordonnee de la case
     */
    public int getY() {
        return this.y;
    }
}
