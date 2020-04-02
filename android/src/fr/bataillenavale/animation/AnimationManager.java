package fr.bataillenavale.animation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class AnimationManager {

    private static AnimationManager instance;

    private HashMap<AnimatedTexture, AnimatedTexture.OnAnimationFinishListener> animations;

    private AnimationManager() {
        this.animations = new HashMap<>();
    }

    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }

    public void clearAnimations() {
        this.animations.clear();
    }

    public void addAnimation(AnimatedTexture animation, AnimatedTexture.OnAnimationFinishListener finishListener) {
        if (animation != null) {
            this.animations.put(animation, finishListener);
        }
    }

    public void renderAnimations(SpriteBatch batch) {
        for (AnimatedTexture animation : this.animations.keySet()) {
            Sprite sprite = animation.getSprite();
            if (sprite != null) {
                sprite.draw(batch);
                if (animation.isFinished()) {
                    AnimatedTexture.OnAnimationFinishListener finishListener = this.animations.get(animation);
                    if (finishListener != null) {
                        finishListener.run();
                    }
                    this.animations.remove(animation);
                }
            } else {
                this.animations.remove(animation);
            }
        }
    }
}
