package fr.bataillenavale.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.bataillenavale.animation.WaterTile;
import fr.bataillenavale.exception.InvalidShipPositionException;
import fr.bataillenavale.exception.InvalidTilePositionException;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Parcelle d'ocean appartenant a un joueur durant une partie
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class OceanChunk {
    // Bateaux dans la parcelle
    private Ship[] ships;
    // Cases d'eaux
    private WaterTile[][] waterTiles;
    // Parties de bateau destruites
    private List<ShipPart> destroyedParts;

    /**
     * Constructeur par defaut de la parcelle d'ocean
     */
    public OceanChunk() {
        this.generateWater();
        this.ships = new Ship[0];
        this.destroyedParts = new ArrayList<>();
    }

    /**
     * Constructeur avec parametre de la parcelle d'ocean
     *
     * @param ships Bateaux presents sur la parcelle
     * @throws InvalidShipPositionException En cas d'erreur de positionnement des bateaux
     */
    public OceanChunk(Ship[] ships) throws InvalidShipPositionException {
        if (ships == null)
            throw new IllegalArgumentException("Ships can't be null !");
        if (ships.length != GameConstants.SHIPS_PER_PLAYER)
            throw new IllegalArgumentException("Bad number of ships !");
        if (!GameUtils.isShipsPlacementCorrect(ships))
            throw new InvalidShipPositionException("Ships positioning is invalid !");
        this.ships = ships;
        this.destroyedParts = new ArrayList<>();
        this.generateWater();
    }

    /**
     * Methode de deserialisation de la parcelle
     *
     * @param inputStream Flux d'entree
     * @return La parcelle d'ocean lue
     * @throws IOException                  En cas d'erreur de lecture sur le flux d'entree
     * @throws InvalidShipPositionException Si le positionnement des bateaux lu est incorrect
     */
    public static OceanChunk deserialize(DataInputStream inputStream) throws IOException, InvalidShipPositionException {
        Ship[] ships = new Ship[GameConstants.SHIPS_PER_PLAYER];
        for (int i = 0; i < ships.length; i++) {
            ships[i] = Ship.deserialize(inputStream);
        }
        return new OceanChunk(ships);
    }

    /**
     * Methode privee permettant de generer les cases d'eau
     */
    private void generateWater() {
        this.waterTiles = new WaterTile[GameConstants.OCEAN_CHUNK_SIZE][GameConstants.OCEAN_CHUNK_SIZE];
        for (int i = 0; i < GameConstants.OCEAN_CHUNK_SIZE; i++) {
            for (int j = 0; j < GameConstants.OCEAN_CHUNK_SIZE; j++) {
                try {
                    this.waterTiles[i][j] = new WaterTile(i, j);
                } catch (InvalidTilePositionException ignored) {
                }
            }
        }
    }

    /**
     * Methode permettant de generer le placement des bateaux aleatoirement
     */
    public void generateShips() {
        this.ships = new Ship[GameConstants.SHIPS_PER_PLAYER];
        for (int i = 0; i < this.ships.length; i++) {
            int shitLength = GameConstants.SHIPS_SIZE_PER_PLAYER[i];
            Direction direction = Direction.getValue(((int) (Math.random() * 4)) + 1);
            Ship ship = null;
            do {
                int x = (int) (Math.random() * GameConstants.OCEAN_CHUNK_SIZE);
                int y = (int) (Math.random() * GameConstants.OCEAN_CHUNK_SIZE);
                try {
                    ship = new Ship(x, y, direction, shitLength);
                } catch (InvalidShipPositionException ignored) {
                }
            } while (ship == null || !this.addShip(ship));
            this.ships[i] = ship;
        }
    }

    /**
     * Methode privee permettant d'ajouter un bateau a la parcelle
     *
     * @param ship Bateau a ajouter a la parcelle
     * @return Vrai si le placement est un succes, faux sinon
     */
    private boolean addShip(Ship ship) {
        int i = 0;
        Ship other;
        while (i < this.ships.length && (other = this.ships[i++]) != null) {
            for (ShipPart otherPart : other.getShipParts()) {
                for (ShipPart shipPart : ship.getShipParts()) {
                    if (otherPart.getX() == shipPart.getX() && otherPart.getY() == shipPart.getY()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Methode permettant de recuperer les parties de bateau detruites de la parcelle
     *
     * @return Les parties de bateau detruites de la parcelle
     */
    public ShipPart[] getDestroyedParts() {
        ShipPart[] destroyedParts = new ShipPart[this.destroyedParts.size()];
        this.destroyedParts.toArray(destroyedParts);
        return destroyedParts;
    }

    public void addDestroyedPart(ShipPart shipPart) {
        if (shipPart != null && shipPart.isDestroyed()) {
            this.destroyedParts.add(shipPart);
        }
    }

    /**
     * Methode permettant de recuperer les bateaux present sur la parcelle
     *
     * @return Les bateaux present sur la parcelle
     */
    public Ship[] getShips() {
        return this.ships;
    }

    /**
     * Methode permettant de recuperer les cases d'eau presentes sur la parcelle
     *
     * @return Les cases d'eau presentes sur la parcelle
     */
    public WaterTile[][] getWaterTiles() {
        return this.waterTiles;
    }

    /**
     * Methode de serialisation de la parcelle d'ocean
     *
     * @param outputStream Flux de sortie
     * @throws IOException En cas d'erreur d'ecriture sur le flux de sortie
     */
    public void serialize(DataOutputStream outputStream) throws IOException {
        for (Ship ship : this.ships) {
            ship.serialize(outputStream);
        }
    }

    public boolean containsWaterTile(WaterTile tile) {
        for (WaterTile[] rows : this.waterTiles) {
            for (WaterTile waterTile : rows) {
                if (waterTile.equals(tile)) {
                    return true;
                }
            }
        }
        return false;
    }


}
