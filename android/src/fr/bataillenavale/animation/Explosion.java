package fr.bataillenavale.animation;

import com.badlogic.gdx.math.Vector2;

import fr.bataillenavale.utils.GameConstants;

public class Explosion extends AnimatedTexture {
    private boolean succes;

    public Explosion(Vector2 position, float frameDuration, boolean succes) {
        super(position, frameDuration, false, GameConstants.ANIMATION_SIZE);
        this.succes = succes;
    }

    @Override
    public boolean isInGame() {
        return true;
    }

    @Override
    public String getTextureName() {
        if(this.succes)
            return "TirSucces";
        else
            return "TirFail";
    }
}
