package fr.bataillenavale.exception;

/**
 * Exception levee lorsque le placement d'un bateau est incorrect
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class InvalidShipPositionException extends Exception {
    /**
     * Constructeur par defaut
     */
    public InvalidShipPositionException() {
        super();
    }

    /**
     * Constructeur avec message
     *
     * @param message Message
     */
    public InvalidShipPositionException(String message) {
        super(message);
    }
}
