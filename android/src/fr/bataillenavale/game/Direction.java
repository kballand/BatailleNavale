package fr.bataillenavale.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Enumeration des directions possibles
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public enum Direction {
    NORTH(1),
    EAST(2),
    SOUTH(3),
    WEST(4);

    // ID de la direction
    private int id;

    /**
     * Constructeur d'une direction
     *
     * @param id ID de la direction
     */
    Direction(int id) {
        this.id = id;
    }

    /**
     * Methode de deserialisation de la direction
     *
     * @param inputStream Flux d'entree
     * @return La direction lue
     * @throws IOException En cas d'erreur de lecture sur le flux
     */
    public static Direction deserialize(DataInputStream inputStream) throws IOException {
        return getValue(inputStream.readInt());
    }

    public static Direction getValue(int x) {
        switch (x) {
            case 1:
                return NORTH;
            case 2:
                return EAST;
            case 3:
                return SOUTH;
            case 4:
                return WEST;
            default:
                return null;
        }
    }

    /**
     * Methode permettant de recuperer l'id de la direction
     *
     * @return L'id de la direction
     */
    public int getId() {
        return this.id;
    }

    /**
     * Methode permettant de serialiser la direction
     *
     * @param outputStream Flux de sortie
     * @throws IOException En cas d'erreur d'ecriture sur le flux de sortie
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.id);
    }
}
