package fr.bataillenavale.exception;

/**
 * Exception levee lorsque le placement d'une case est invalide
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class InvalidTilePositionException extends Exception {
    /**
     * Constructeur par defaut
     */
    public InvalidTilePositionException() {
        super();
    }

    /**
     * Constructeur avec message
     *
     * @param message Message
     */
    public InvalidTilePositionException(String message) {
        super(message);
    }
}