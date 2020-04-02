package fr.bataillenavale.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import fr.bataillenavale.game.BatailleNavale;
import fr.bataillenavale.utils.GameConstants;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe d'affichage pour le menu d'options
 */
public class OptionsScreen implements Screen {

	/**
	 * ecran precedent
	 */
    private Screen previousScreen;
    /**
     * scene d'affichage
     */
    private Stage stage;
    /**
     * table
     */
    private Table table;
    /**
     * pseudonyme du joueur
     */
    private TextField username;
    /**
     * bouton pour valider le pseudo
     */
    private TextButton validateUsername;
    /**
     * pourcentage de volume
     */
    private Label volumeIndicator;
    /**
     * barre du volume
     */
    private Slider volumeBar;
    /**
     * cas epour cocher decocher les animations
     */
    private CheckBox animations;
    /**
     * bouton retour
     */
    private TextButton back;

    /**
     * constructeur de OptionsScreen
     * @param previousScreen Screen
     */
    public OptionsScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
        this.stage = new Stage(BatailleNavale.getInstance().getViewport());
        this.table = new Table();
        this.stage.addActor(this.table);
        this.username = new TextField("   " + BatailleNavale.getInstance().getUsername(), GameUtils.getDefaultSkin());
        this.username.setMessageText("Entrez votre pseudo...");
        this.username.setMaxLength(15);
        this.username.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.username.setAlignment(Align.left);
        this.validateUsername = new TextButton("Changer le pseudo", GameUtils.getDefaultSkin());
        this.validateUsername.pad(30);
        this.validateUsername.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.validateUsername.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (OptionsScreen.this.username.getText().trim().isEmpty()) {
                    BatailleNavale.getInstance().showText("Votre pseudo ne peut être vide !", false);
                } else {
                    BatailleNavale.getInstance().setUsername(OptionsScreen.this.username.getText());
                }
            }
        });
        this.back = new TextButton("Retour", GameUtils.getDefaultSkin());
        this.back.pad(30);
        this.back.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                BatailleNavale.getInstance().setScreen(OptionsScreen.this.previousScreen);
                OptionsScreen.this.previousScreen.resume();
                OptionsScreen.this.dispose();
            }
        });
        this.volumeIndicator = new Label("Volume : " + BatailleNavale.getInstance().getVolume() + "%", new Label.LabelStyle(GameUtils.getDefaultSkin().getFont("button"), Color.BLACK));
        this.volumeIndicator.getStyle().font.getData().setScale(GameConstants.BUTTON_SCALE);
        this.volumeBar = new Slider(0, 100, 1, false, GameUtils.getDefaultSkin());
        this.volumeBar.getStyle().knob.setMinHeight(40);
        this.volumeBar.getStyle().knob.setMinWidth(25);
        this.volumeBar.setValue(BatailleNavale.getInstance().getVolume());
        this.volumeBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int volume = (int) OptionsScreen.this.volumeBar.getValue();
                BatailleNavale.getInstance().setVolume(volume);
                OptionsScreen.this.volumeIndicator.setText("Volume : " + volume + "%");
            }
        });
        this.animations = new CheckBox("Animations", GameUtils.getDefaultSkin());
        this.animations.setChecked(BatailleNavale.getInstance().isAnimated());
        this.animations.getImageCell().size(50, 50);
        this.animations.getStyle().font.getData().setScale(GameConstants.FONT_SCALE);
        this.animations.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BatailleNavale.getInstance().setAnimated(OptionsScreen.this.animations.isChecked());
            }
        });
        this.table.setFillParent(true);
        this.table.add(this.username).size(GameConstants.SCREEN_WIDTH * 0.65f, GameConstants.SCREEN_HEIGHT * 0.075f).pad(20);
        this.table.row();
        this.table.add(this.validateUsername).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(20);
        this.table.row();
        this.table.add(this.volumeIndicator).pad(20).padTop(50);
        this.table.row();
        this.table.add(this.volumeBar).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(20);
        this.table.row();
        this.table.add(this.animations).height(100).width(100).pad(20);
        this.table.row();
        this.table.add(this.back).size(GameConstants.SCREEN_WIDTH * 0.6f, GameConstants.SCREEN_HEIGHT * 0.07f).pad(20).padTop(100);
        this.table.row();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            BatailleNavale.getInstance().setScreen(this.previousScreen);
            this.previousScreen.resume();
            this.dispose();
        }
        this.stage.act(delta);
        this.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
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
        this.stage.dispose();
    }


}
