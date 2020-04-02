package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Set;

import fr.bataillenavale.animation.AnimatedTexture;
import fr.bataillenavale.animation.AnimationManager;
import fr.bataillenavale.animation.Explosion;
import fr.bataillenavale.animation.Tir;
import fr.bataillenavale.animation.WaterTile;
import fr.bataillenavale.connection.Connection;
import fr.bataillenavale.connection.ConnectionManager;
import fr.bataillenavale.connection.ServerConnection;
import fr.bataillenavale.connection.ServerSenderReceiver;
import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.game.OceanChunk;
import fr.bataillenavale.game.Ship;
import fr.bataillenavale.game.ShipPart;
import fr.bataillenavale.models.ClientPlayerModel;
import fr.bataillenavale.models.LobbyModel;
import fr.bataillenavale.models.PlayerModel;
import fr.bataillenavale.packet.NextTurnPacket;
import fr.bataillenavale.packet.PlayerHasLostPacket;
import fr.bataillenavale.packet.ShipPartDestroyedPacket;
import fr.bataillenavale.packet.ShotTilePacket;
import fr.bataillenavale.packet.WaterTileDestroyedPacket;
import fr.bataillenavale.task.DelayedTask;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

import static fr.bataillenavale.utils.GameConstants.OCEAN_CHUNK_SIZE;
import static fr.bataillenavale.utils.GameConstants.SCREEN_HEIGHT;
import static fr.bataillenavale.utils.GameConstants.SCREEN_WIDTH;

/**
 * Classe pour l'affichage du jeu
 */
public class GameScreen implements Screen {
	/**
     * modele du salon
     */
    private LobbyModel lobbyModel;
    /**
     * carte du jeu
     */
    private HashMap<WaterTile, Rectangle> waterTilesBox;

    /**
     * constructeur de GameScreen
     * @param lobbyModel
     */
    public GameScreen(LobbyModel lobbyModel) {
        this.lobbyModel = lobbyModel;
        this.waterTilesBox = new HashMap<>();
    }

    @Override
    public void show() {
        GestureDetector.GestureListener listener = new GestureDetector.GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                Connection connection = ConnectionManager.getInstance().getConnection();
                if(connection != null) {
                    final ClientPlayerModel playerModel = connection.getPlayer();
                    if(playerModel != null) {
                        final LobbyModel lobby = playerModel.getLobby();
                        if (lobby != null) {
                            if (playerModel.equals(lobby.getPlayerTurn())) {
                                OrthographicCamera camera = (OrthographicCamera) BatailleNavale.getInstance().getViewport().getCamera();
                                float zoom = camera.zoom;
                                Vector3 position = camera.position;
                                float cameraX = position.x;
                                float cameraY = position.y;
                                float touchedX = cameraX + ((x - (camera.viewportWidth / 2)) * zoom);
                                float touchedY = cameraY - ((y - (camera.viewportHeight / 2)) * zoom);
                                Set<WaterTile> waterTiles = waterTilesBox.keySet();
                                WaterTile selected = null;
                                for (WaterTile waterTile : waterTiles) {
                                    Rectangle boundingRectangle = waterTilesBox.get(waterTile);
                                    if (boundingRectangle != null && boundingRectangle.contains(touchedX, touchedY)) {
                                        selected = waterTile;
                                        break;
                                    }
                                }
                                WaterTile previousSelected = playerModel.getSelectedTile();
                                if (selected == null || selected.equals(previousSelected)) {
                                    if (selected != null) {
                                        final PlayerModel tileOwner = lobby.getTileOwner(selected);
                                        if (tileOwner != null) {
                                            if (connection instanceof ServerConnection) {
                                                final ServerConnection serverConnection = (ServerConnection) connection;
                                                final OceanChunk touchedOcean = tileOwner.getOceanChunk();
                                                if (touchedOcean != null) {
                                                    boolean hasDestroyedPart = false;
                                                    Ship[] touchedShips = touchedOcean.getShips();
                                                    for (Ship touchedShip : touchedShips) {
                                                        for (final ShipPart shipPart : touchedShip.getShipParts()) {
                                                            if (shipPart.getX() == selected.getX() && shipPart.getY() == selected.getY()) {
                                                                if (!shipPart.isDestroyed()) {
                                                                    connection.sendPacket(new ShipPartDestroyedPacket(tileOwner.getPlayerUUID(), shipPart));
                                                                    lobby.resetPlayerTurn();
                                                                    Vector2 tileCenter = GameUtils.getTileCenter(lobby, tileOwner, selected.getX(), selected.getY());
                                                                    final Vector2 finalPosition = new Vector2(tileCenter.x - (GameConstants.ANIMATION_SIZE / 2), tileCenter.y + (GameConstants.ANIMATION_SIZE / 2));
                                                                    Tir tir = new Tir(new Vector2(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT), finalPosition, 0.08f);
                                                                    AnimationManager.getInstance().addAnimation(tir, new AnimatedTexture.OnAnimationFinishListener() {
                                                                        @Override
                                                                        public void run() {
                                                                            Explosion explosion = new Explosion(finalPosition, 0.1f, true);
                                                                            AnimationManager.getInstance().addAnimation(explosion, new AnimatedTexture.OnAnimationFinishListener() {
                                                                                @Override
                                                                                public void run() {
                                                                                    shipPart.setDestroyed(true);
                                                                                    touchedOcean.addDestroyedPart(shipPart);
                                                                                    new DelayedTask(2000) {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            boolean hasLost = true;
                                                                                            for(Ship s : touchedOcean.getShips()) {
                                                                                                for(ShipPart sp : s.getShipParts()) {
                                                                                                    if(!sp.isDestroyed()) {
                                                                                                        hasLost = false;
                                                                                                        break;
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            if(!hasLost) {
                                                                                                lobby.nextTurn();
                                                                                                PlayerModel playerTurn = lobby.getPlayerTurn();
                                                                                                if (playerTurn.equals(playerModel)) {
                                                                                                    BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                                                                                                } else {
                                                                                                    BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                                                                                                }
                                                                                                for (ServerSenderReceiver client : serverConnection.getClients()) {
                                                                                                    client.send(new NextTurnPacket());
                                                                                                }
                                                                                            } else {
                                                                                                tileOwner.setLost(true);
                                                                                                BatailleNavale.getInstance().showText("Le joueur " + tileOwner.getUsername() + " a perdu !", true);
                                                                                                for (ServerSenderReceiver client : serverConnection.getClients()) {
                                                                                                    client.send(new PlayerHasLostPacket(tileOwner.getPlayerUUID()));
                                                                                                }
                                                                                                new DelayedTask(2000) {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        lobby.nextTurn();
                                                                                                        PlayerModel playerTurn = lobby.getPlayerTurn();
                                                                                                        if (playerTurn.equals(playerModel)) {
                                                                                                            BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                                                                                                        } else {
                                                                                                            BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                                                                                                        }
                                                                                                        for (ServerSenderReceiver client : serverConnection.getClients()) {
                                                                                                            client.send(new NextTurnPacket());
                                                                                                        }
                                                                                                    }
                                                                                                }.start();
                                                                                            }
                                                                                        }
                                                                                    }.start();
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                                hasDestroyedPart = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (!hasDestroyedPart) {
                                                        WaterTile[][] touchedTiles = touchedOcean.getWaterTiles();
                                                        final WaterTile touchedTile = touchedTiles[selected.getX()][selected.getY()];
                                                        if (!touchedTile.isDestroyed()) {
                                                            connection.sendPacket(new WaterTileDestroyedPacket(tileOwner.getPlayerUUID(), selected.getX(), selected.getY()));
                                                            lobby.resetPlayerTurn();
                                                            Vector2 tileCenter = GameUtils.getTileCenter(lobby, tileOwner, selected.getX(), selected.getY());
                                                            final Vector2 finalPosition = new Vector2(tileCenter.x - (GameConstants.ANIMATION_SIZE / 2), tileCenter.y + (GameConstants.ANIMATION_SIZE / 2));
                                                            Tir tir = new Tir(new Vector2(GameConstants.SCREEN_WIDTH / 2, GameConstants.SCREEN_HEIGHT), finalPosition, 0.08f);
                                                            AnimationManager.getInstance().addAnimation(tir, new AnimatedTexture.OnAnimationFinishListener() {
                                                                @Override
                                                                public void run() {
                                                                    Explosion explosion = new Explosion(new Vector2(600, 600), 0.1f, false);
                                                                    AnimationManager.getInstance().addAnimation(explosion, new AnimatedTexture.OnAnimationFinishListener() {
                                                                        @Override
                                                                        public void run() {
                                                                            touchedTile.setDestroyed(true);
                                                                            new DelayedTask(2000) {
                                                                                @Override
                                                                                public void run() {
                                                                                    lobby.nextTurn();
                                                                                    PlayerModel playerTurn = lobby.getPlayerTurn();
                                                                                    if (playerTurn.equals(playerModel)) {
                                                                                        BatailleNavale.getInstance().showText("C'est à votre tour de jouer, vous avez 60 secondes pour tirer sur une case adverse en cliquant dessus.", true);
                                                                                    } else {
                                                                                        BatailleNavale.getInstance().showText("C'est au tour de " + playerTurn.getUsername() + " de jouer.", true);
                                                                                    }
                                                                                    for (ServerSenderReceiver client : serverConnection.getClients()) {
                                                                                        client.send(new NextTurnPacket());
                                                                                    }
                                                                                }
                                                                            }.start();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            } else {
                                                connection.sendPacket(new ShotTilePacket(tileOwner.getPlayerUUID(), selected.getX(), selected.getY()));
                                            }
                                        }
                                    }
                                    playerModel.setSelectedTile(null);
                                } else {
                                    PlayerModel tileOwner = lobby.getTileOwner(selected);
                                    if (tileOwner != null) {
                                        OceanChunk touchedOcean = tileOwner.getOceanChunk();
                                        if (touchedOcean != null) {
                                            ShipPart[] destroyedParts = touchedOcean.getDestroyedParts();
                                            boolean hasDestroyedPart = false;
                                            for (ShipPart destroyedPart : destroyedParts) {
                                                if (destroyedPart.getX() == selected.getX() && destroyedPart.getY() == selected.getY()) {
                                                    hasDestroyedPart = true;
                                                    break;
                                                }
                                            }
                                            if (!hasDestroyedPart) {
                                                WaterTile[][] touchedTiles = touchedOcean.getWaterTiles();
                                                final WaterTile touchedTile = touchedTiles[selected.getX()][selected.getY()];
                                                if (!touchedTile.isDestroyed()) {
                                                    BatailleNavale.getInstance().showText("Réappuyez une nouvelle fois sur la case selectionnée pour tirer dessus.", false);
                                                    playerModel.setSelectedTile(selected);
                                                }
                                            }
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                return true;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                OrthographicCamera camera = (OrthographicCamera) BatailleNavale.getInstance().getViewport().getCamera();
                float zoom = camera.zoom;
                float minX = (camera.viewportWidth / 2) * zoom;
                float minY = (camera.viewportHeight / 2) * zoom;
                float maxX = camera.viewportWidth - minX;
                float maxY = camera.viewportHeight - minY;
                float dX = -deltaX * zoom;
                float dY = deltaY * zoom;
                Vector3 position = camera.position;
                float finalX = position.x + dX;
                float finalY = position.y + dY;
                if (finalX > maxX) {
                    dX = maxX - position.x;
                } else if (finalX < minX) {
                    dX = 0;
                }
                if (finalY > maxY) {
                    dY = maxY - position.y;
                } else if (finalY < minY) {
                    dY = 0;
                }
                camera.translate(dX, dY);
                return true;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                OrthographicCamera camera = (OrthographicCamera) BatailleNavale.getInstance().getViewport().getCamera();
                float zoom = camera.zoom + (((distance / initialDistance) - 1) / 20f);
                if (zoom > 1) {
                    zoom = 1;
                } else if (zoom < 0.2f) {
                    zoom = 0.2f;
                }
                camera.zoom = zoom;
                float minX = (camera.viewportWidth / 2) * zoom;
                float minY = (camera.viewportHeight / 2) * zoom;
                float maxX = camera.viewportWidth - minX;
                float maxY = camera.viewportHeight - minY;
                Vector3 position = camera.position;
                if (position.x > maxX) {
                    position.x = maxX;
                } else if (position.x < minX) {
                    position.x = minX;
                }
                if (position.y > maxY) {
                    position.y = maxY;
                } else if (position.y < minY) {
                    position.y = minY;
                }
                return true;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {

            }
        };
        Gdx.input.setInputProcessor(new GestureDetector(listener));
    }

    @Override
    public void render(float delta) {
        this.waterTilesBox.clear();
        Camera camera = BatailleNavale.getInstance().getViewport().getCamera();
        camera.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        PlayerModel[] playerModels = this.lobbyModel.getRemainingPlayers();
        int numberOfPlayers = playerModels.length;
        int numberOfPeers = (int) Math.ceil(numberOfPlayers / 2F);
        if (numberOfPeers == 0)
            numberOfPeers = 1;
        boolean odd = numberOfPeers != numberOfPlayers / 2F;

        float gridSize = SCREEN_WIDTH / numberOfPeers;
        float gridMaxHeight = SCREEN_HEIGHT / (numberOfPlayers > 1 ? 1 : 2);

        if ((gridSize > gridMaxHeight / 2) && numberOfPlayers > 1) {
            gridSize = gridMaxHeight / 2;
        } else if (gridSize > gridMaxHeight) {
            gridSize = gridMaxHeight;
        }

        float separatorLineWidth = gridSize * 0.01F;
        float separatorMargin = gridSize * 0.025F;
        float tileMargin = gridSize * 0.0075F;

        ShapeRenderer shapeRenderer = BatailleNavale.getInstance().getShapeRenderer();
        shapeRenderer.setProjectionMatrix(BatailleNavale.getInstance().getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);

        float x;
        float finalX;
        float y;
        float finalY;
        if (numberOfPlayers > 1) {
            x = separatorMargin;
            finalX = SCREEN_WIDTH - separatorMargin;
            if (odd) {
                finalX = SCREEN_WIDTH - gridSize;
            }
            y = SCREEN_HEIGHT / 2;
            finalY = y;
            shapeRenderer.rectLine(x, y, finalX, finalY, separatorLineWidth);
        }


        for (int i = 0; i < numberOfPeers - 1; i++) {
            x = (i + 1) * (SCREEN_WIDTH / numberOfPeers);
            finalX = x;
            y = (SCREEN_HEIGHT) / 2 - gridSize + separatorMargin;
            finalY = (SCREEN_HEIGHT) / 2 + gridSize - separatorMargin;
            shapeRenderer.rectLine(x, y, finalX, finalY, separatorLineWidth);
        }

        float tileSize = ((gridSize - (2 * separatorMargin) - ((OCEAN_CHUNK_SIZE - 1) * tileMargin)) / OCEAN_CHUNK_SIZE);


        SpriteBatch spriteBatch = BatailleNavale.getInstance().getSpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        float extraX = SCREEN_WIDTH - gridSize * numberOfPeers;
        for (int k = 0; k < numberOfPlayers; k++) {
            int posX = k / 2;
            int posY = k % 2;
            PlayerModel playerModel = playerModels[k];
            OceanChunk oceanChunk = playerModel.getOceanChunk();
            WaterTile[][] waterTiles = oceanChunk.getWaterTiles();
            Connection connection = ConnectionManager.getInstance().getConnection();
            if (connection != null) {
                ClientPlayerModel clientPlayer = connection.getPlayer();
                if (clientPlayer != null) {
                    WaterTile selectedTile = clientPlayer.getSelectedTile();
                    for (int i = 0; i < OCEAN_CHUNK_SIZE; i++) {
                        x = gridSize * posX + separatorMargin + i * (tileSize + tileMargin) + extraX / 2;
                        for (int j = 0; j < OCEAN_CHUNK_SIZE; j++) {
                            if (k == numberOfPlayers - 1 && odd) {
                                y = ((SCREEN_HEIGHT / 2) + (gridSize / 2)) - (separatorMargin + j * (tileSize + tileMargin) + tileSize);
                            } else {
                                y = ((SCREEN_HEIGHT / 2) + posY * gridSize) - (separatorMargin + j * (tileSize + tileMargin) + tileSize);
                            }
                            WaterTile waterTile = waterTiles[i][j];
                            if(waterTile.equals(selectedTile)) {
                                float borderWidth = tileMargin / 2f;
                                shapeRenderer.setColor(Color.RED);
                                shapeRenderer.rect(x - borderWidth, y - borderWidth, borderWidth, tileSize + 2 * borderWidth);
                                shapeRenderer.rect(x, y - borderWidth, tileSize + borderWidth, borderWidth);
                                shapeRenderer.rect(x + tileSize, y - borderWidth, borderWidth, tileSize + 2 * borderWidth);
                                shapeRenderer.rect(x, y + tileSize, tileSize, borderWidth);
                            }
                            Sprite waterSprite = waterTile.getSprite();
                            waterSprite.setPosition(x, y);
                            waterSprite.setSize(tileSize, tileSize);
                            waterSprite.draw(spriteBatch);
                            if(!clientPlayer.equals(playerModel)) {
                                this.waterTilesBox.put(waterTile, waterSprite.getBoundingRectangle());
                            }
                        }
                    }

                    if (clientPlayer.equals(playerModel)) {
                        Ship[] ships = clientPlayer.getOceanChunk().getShips();
                        for (Ship ship : ships) {
                            ShipPart[] shipParts = ship.getShipParts();
                            for (ShipPart shipPart : shipParts) {
                                Sprite partSprite = shipPart.getSprite();
                                x = gridSize * posX + separatorMargin + shipPart.getX() * (tileSize + tileMargin) + extraX / 2;
                                if (k == numberOfPlayers - 1 && odd) {
                                    y = ((SCREEN_HEIGHT / 2) + (gridSize / 2)) - (separatorMargin + shipPart.getY() * (tileSize + tileMargin) + tileSize);
                                } else {
                                    y = ((SCREEN_HEIGHT / 2) + posY * gridSize) - (separatorMargin + shipPart.getY() * (tileSize + tileMargin) + tileSize);
                                }
                                partSprite.setPosition(x, y);
                                partSprite.setSize(tileSize, tileSize);
                                partSprite.draw(spriteBatch);
                            }
                        }
                    } else {
                        ShipPart[] shipParts = oceanChunk.getDestroyedParts();
                        for (ShipPart shipPart : shipParts) {
                            Sprite partSprite = shipPart.getSprite();
                            x = gridSize * posX + separatorMargin + shipPart.getX() * (tileSize + tileMargin) + extraX / 2;
                            if (k == numberOfPlayers - 1 && odd) {
                                y = ((SCREEN_HEIGHT / 2) + (gridSize / 2)) - (separatorMargin + shipPart.getY() * (tileSize + tileMargin) + tileSize);
                            } else {
                                y = ((SCREEN_HEIGHT / 2) + posY * gridSize) - (separatorMargin + shipPart.getY() * (tileSize + tileMargin) + tileSize);
                            }
                            partSprite.setPosition(x, y);
                            partSprite.setSize(tileSize, tileSize);
                            partSprite.draw(spriteBatch);
                        }
                    }
                }
            }
        }
        AnimationManager.getInstance().renderAnimations(spriteBatch);
        spriteBatch.end();
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        BatailleNavale.getInstance().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        OrthographicCamera camera = (OrthographicCamera) BatailleNavale.getInstance().getViewport().getCamera();
        camera.zoom = 1.0f;
        camera.setToOrtho(false, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        camera.update();
        AnimationManager.getInstance().clearAnimations();
    }
}
