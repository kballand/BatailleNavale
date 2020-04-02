package fr.bataillenavale.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.bataillenavale.exception.InvalidTilePositionException;
import fr.bataillenavale.utils.GameUtils;

/**
 * Partie de bateau
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class ShipPart extends Tile {
    // Nom de la partie de bateau
    private String partName;
    // Direction de la partie de bateau
    private Direction direction;
    // Indique si la partie de bateau est detruite ou non
    private boolean destroyed;

    /**
     * Constructeur d'une partie de bateau
     *
     * @param x         Abscisse de la partie de bateau
     * @param y         Ordonnee de la partie de bateau
     * @param partName  Nom de la partie de bateau
     * @param direction Direction de la partie de bateau
     * @throws InvalidTilePositionException En cas de placement de la partie invalide
     */
    public ShipPart(int x, int y, String partName, Direction direction) throws InvalidTilePositionException {
        this(x, y, partName, direction, false);
    }

    /**
     * Constructeur d'une partie de bateau
     *
     * @param x         Abscisse de la partie de bateau
     * @param y         Ordonnee de la partie de bateau
     * @param partName  Nom de la partie de bateau
     * @param direction Direction de la partie de bateau
     * @param destroyed Booleen indique si le partie de bateau est detruite ou non
     * @throws InvalidTilePositionException En cas de placement de la partie invalide
     */
    public ShipPart(int x, int y, String partName, Direction direction, boolean destroyed) throws InvalidTilePositionException {
        super(x, y);
        this.partName = partName;
        this.direction = direction;
        this.destroyed = destroyed;
    }

    /**
     * Methode permettant de deserialiser une partie de bateau
     *
     * @param inputStream Flux d'entree
     * @return La partie de bateau lue
     * @throws IOException                  En cas d'erreur de lecture sur le flux d'entree
     * @throws InvalidTilePositionException En cas de position invalide de la partie de bateau
     */
    public static ShipPart deserialize(DataInputStream inputStream) throws IOException, InvalidTilePositionException {
        int x = inputStream.readInt();
        int y = inputStream.readInt();
        String partName = inputStream.readUTF();
        Direction direction = Direction.deserialize(inputStream);
        boolean destroyed = inputStream.readBoolean();
        return new ShipPart(x, y, partName, direction, destroyed);
    }

    /**
     * Methode permettant de savoir si la partie de bateau est detruit ou non
     *
     * @return Vrai si la partie de bateau est detruite, faux sinon
     */
    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    /**
     * Methode permettant de recuperer la direction de la partie de bateau
     *
     * @return La direction de la partie de bateau
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Methode de serialisation de la partie de bateau
     *
     * @param outputStream Flux de sortie
     * @throws IOException En cas d'erreur d'ecriture sur le flux de sortie
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.getX());
        outputStream.writeInt(this.getY());
        outputStream.writeUTF(this.partName);
        this.direction.serialize(outputStream);
        outputStream.writeBoolean(this.destroyed);
    }

    @Override
    public boolean isInGame() {
        return true;
    }

    @Override
    public String getTextureName() {
        return this.partName + (this.destroyed ? "_feu" : "");
    }

    @Override
    public Sprite getSprite() {
        Texture texture = GameUtils.getTexture(this.getTextureName() + ".png", this.isInGame());
        Sprite sprite = new Sprite(texture);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        for (int i = 0; i < this.direction.getId() - 1; i++) {
            sprite.rotate90(false);
        }
        return sprite;
    }
}
