package fr.bataillenavale.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.bataillenavale.exception.InvalidShipPositionException;
import fr.bataillenavale.exception.InvalidTilePositionException;
import fr.bataillenavale.utils.GameConstants;

/**
 * Representantation d'un bateau
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class Ship {
    // Abscisse du bateaux
    private int x;
    // Ordonnee du bateau
    private int y;
    // Longueur du bateau
    private int length;
    // Nom du bateau
    private String shipName;
    // Parties du bateau
    private ShipPart[] shipParts;
    // Direction du bateau
    private Direction direction;

    /**
     * Constructeur d'un bateau
     *
     * @param x         Abscisse du bateau
     * @param y         Ordonnee du bateau
     * @param direction Direction du bateau
     * @param length    Longueur du bateau
     * @throws InvalidShipPositionException Si la position du bateau est invalide
     */
    public Ship(int x, int y, Direction direction, int length) throws InvalidShipPositionException {
        if (direction == null)
            throw new IllegalArgumentException("Ship direction can't be null !");
        if (length < GameConstants.MIN_SHIP_LENGTH || length > GameConstants.MAX_SHIP_LENGTH)
            throw new IllegalArgumentException("Ship length is invalid !");
        this.length = length;
        this.shipName = GameConstants.SHIPS_NAME[this.length - GameConstants.MIN_SHIP_LENGTH];
        this.shipParts = new ShipPart[length];
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.updateShipParts();
    }

    /**
     * Constructeur prive d'un bateau
     *
     * @param x         Abscisse du bateau
     * @param y         Ordonnee du bateau
     * @param direction Direction du bateau
     * @param shipParts Parties du bateau
     */
    private Ship(int x, int y, Direction direction, ShipPart[] shipParts) {
        this.length = shipParts.length;
        this.shipName = GameConstants.SHIPS_NAME[this.length - GameConstants.MIN_SHIP_LENGTH];
        this.shipParts = shipParts;
        this.direction = direction;
        this.x = x;
        this.y = y;
    }

    /**
     * Methode de deserialisation d'un bateau
     *
     * @param inputStream Flux d'entree
     * @return Le bateau lu
     * @throws IOException                  En cas d'erreur de lecture sur le flux d'entree
     * @throws InvalidShipPositionException En cas de position du bateau invalide
     */
    public static Ship deserialize(DataInputStream inputStream) throws IOException, InvalidShipPositionException {
        int x = inputStream.readInt();
        int y = inputStream.readInt();
        int length = inputStream.readInt();
        ShipPart[] shipParts = new ShipPart[length];
        for (int i = 0; i < length; i++) {
            try {
                shipParts[i] = ShipPart.deserialize(inputStream);
            } catch (InvalidTilePositionException e) {
                throw new InvalidShipPositionException();
            }
        }
        Direction direction = Direction.deserialize(inputStream);
        return new Ship(x, y, direction, shipParts);
    }

    /**
     * Methode privee de mise a jour des parties du bateau
     *
     * @throws InvalidShipPositionException En cas de position du bateau invalide
     */
    private void updateShipParts() throws InvalidShipPositionException {
        try {
            switch (this.direction) {
                case NORTH:
                    for (int i = 0; i < this.length; i++) {
                        this.shipParts[i] = new ShipPart(this.x, this.y + i, this.shipName + (i + 1), this.direction);
                    }
                    break;
                case EAST:
                    for (int i = 0; i < this.length; i++) {
                        this.shipParts[i] = new ShipPart(this.x + i, this.y, this.shipName + (i + 1), this.direction);
                    }
                    break;
                case SOUTH:
                    for (int i = 0; i < this.length; i++) {
                        this.shipParts[i] = new ShipPart(this.x, this.y - i, this.shipName + (i + 1), this.direction);
                    }
                    break;
                case WEST:
                    for (int i = 0; i < this.length; i++) {
                        this.shipParts[i] = new ShipPart(this.x - i, this.y, this.shipName + (i + 1), this.direction);
                    }
            }
        } catch (InvalidTilePositionException e) {
            throw new InvalidShipPositionException();
        }
    }

    /**
     * Methode permettant de recuperer les parties du bateau
     *
     * @return Les parties du bateau
     */
    public ShipPart[] getShipParts() {
        return this.shipParts;
    }

    /**
     * Methode permettant de recuperer la longueur du bateau
     *
     * @return La longueur du bateau
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Methode permettant de savoir si le bateau est detruit
     *
     * @return Vrai si le bateau est detruit, faux sinon
     */
    public boolean isDestroyed() {
        for (ShipPart p : this.shipParts) {
            if (!p.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Methode permettant de recuperer la direction du bateau
     *
     * @return La direction du bateau
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Methode de serialisation du bateau
     *
     * @param outputStream Flux de sortie
     * @throws IOException En cas d'erreur d'ecriture sur le flux de sortie
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.x);
        outputStream.writeInt(this.y);
        outputStream.writeInt(this.length);
        for (ShipPart sp : this.shipParts) {
            sp.serialize(outputStream);
        }
        this.direction.serialize(outputStream);
    }
}
